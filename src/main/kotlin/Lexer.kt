object Lexer {
    operator fun invoke(function: String): List<Token> = ExprConcat(TokenConcat(Tokenizer(function)))

    object Tokenizer {
        operator fun invoke(function: String): List<Token> {
            val tokens = mutableListOf<Token>()

            var lastToken: Token = ExprStartToken().also { tokens.add(it) }

            var expressionStarts = 1

            val iterator = function.trimAll().iterator()
            for (c in iterator) {
                val token: Token = when (lastToken) {
                    is ExprStartToken, is OperatorToken -> tokenOf(c, TokenType.NUMBER, TokenType.NAME, TokenType.SIGN, TokenType.EXPR_START)
                    is ExprEndToken -> tokenOf(c, TokenType.OPERATOR, TokenType.EXPR_END)
                    is NumberToken -> tokenOf(c, TokenType.NUMBER, TokenType.DOT, TokenType.OPERATOR, TokenType.EXPR_END)
                    is DotToken -> tokenOf(c, TokenType.NUMBER)
                    is FunctionToken -> tokenOf(c, TokenType.EXPR_START)
                    is NameToken -> tokenOf(c, TokenType.OPERATOR, TokenType.EXPR_END, TokenType.NAME, TokenType.EXPR_START)
                    is SignToken -> tokenOf(c, TokenType.SIGN, TokenType.NUMBER, TokenType.NAME, TokenType.EXPR_START)
                    else -> null
                } ?: throw SyntaxError("Unexpected token '$c'")
                lastToken = token
                tokens.add(token)
                when (token) {
                    is ExprStartToken -> ++expressionStarts
                    is ExprEndToken -> --expressionStarts
                }
            }
            repeat(expressionStarts) {
                tokens.add(ExprEndToken())
            }

            return tokens
        }

        private fun tokenOf(c: Char, vararg accepted: TokenType): Token? {
            if (accepted.contains(TokenType.NUMBER)) {
                if (c in "1234567890") return NumberToken(c.toString().toDouble())
            }
            if (accepted.contains(TokenType.DOT)) {
                if (c in ".") return DotToken()
            }
            if (accepted.contains(TokenType.OPERATOR)) {
                if (c in "+-*/%^") return OperatorToken(Operator.ofChar(c))
            }
            if (accepted.contains(TokenType.SIGN)) {
                if (c in "+-") return SignToken(c == '+')
            }
            if (accepted.contains(TokenType.EXPR_START)) {
                if (c in "(") return ExprStartToken()
            }
            if (accepted.contains(TokenType.EXPR_END)) {
                if (c in ")") return ExprEndToken()
            }
            if (accepted.contains(TokenType.NAME)) {
                if (c in "abcdefghijklmnopqrstuvwxyz") return NameToken(c.toString())
            }
            return null
        }

        private fun String.trimAll(): String {
            val output = StringBuilder()
            for (c in this) {
                if (c !in " \t\n\r") output.append(c)
            }
            return output.toString()
        }
    }

    object TokenConcat {
        operator fun invoke(tokens: List<Token>): List<Token> {
            val output = mutableListOf<Token>()

            var i = 0
            while (i < tokens.size) {
                val token = tokens[i]
                if (token is NameToken) {
                    val sb = StringBuilder().append(token.name)
                    var function = false
                    varConcat@do {
                        val next = tokens[i+1]
                        if (next is NameToken) {
                            sb.append(next.name)
                        } else if (next is ExprStartToken) {
                            function = true
                            break@varConcat
                        } else break@varConcat
                    } while (++i < tokens.size - 1)
                    output.add(if (function) FunStartToken(Function.ofString(sb.toString())) else VarToken(sb.toString()))
                } else if (token is SignToken) {
                    val newToken = NumberToken(if (token.sign) 1.0 else -1.0)
                    signConcat@do {
                        val next = tokens[i + 1]
                        if (next is SignToken) {
                            newToken.number = newToken.number * (if (next.sign) 1 else -1)
                        } else if (next is NumberToken) {
                            newToken.number = newToken.number * next.number
                            var afterDot = false
                            var decAfterDot = 0
                            while (++i < tokens.size - 1) {
                                val nextNumber = tokens[i + 1]
                                if (nextNumber is NumberToken) {
                                    newToken.number = newToken.number * 10 + nextNumber.number
                                    if (afterDot) ++decAfterDot
                                } else if (next is DotToken) {
                                    if (afterDot) throw SyntaxError("Unexpected token '.'")
                                    afterDot = true
                                } else break@signConcat
                            }
                            repeat(decAfterDot) { newToken.number /= 10.0 }
                            output.add(newToken)
                        } else break@signConcat
                    } while (++i < tokens.size - 1)
                    output.add(newToken)
                } else if (token is NumberToken) {
                    val newToken = NumberToken(token.number)
                    var afterDot = false
                    var decAfterDot = 0
                    numberConcat@do {
                        val next = tokens[i + 1]
                        if (next is NumberToken) {
                            newToken.number = newToken.number * 10 + next.number
                            if (afterDot) ++decAfterDot
                        } else if (next is DotToken) {
                            if (afterDot) throw SyntaxError("Unexpected token '.'")
                            afterDot = true
                        } else break@numberConcat
                    } while (++i < tokens.size - 1)
                    repeat(decAfterDot) { newToken.number /= 10.0 }
                    output.add(newToken)
                } else {
                    output.add(token)
                }
                ++i
            }

            output.removeAt(0)
            output.removeAt(output.size - 1)

            return output
        }
    }

    object ExprConcat {
        operator fun invoke(tokens: List<Token>): List<Token> {
            val output = mutableListOf<Token>()
            var subExpr = mutableListOf<Token>()
            var function: Function? = null
            var inFun = false
            var inSub = false

            for (token in tokens) {
                if (token is FunStartToken) {
                    function = token.function
                    inFun = true
                    continue
                }
                if (token is ExprStartToken) {
                    subExpr = mutableListOf()
                    inSub = true
                    continue
                }
                if (token is ExprEndToken) {
                    if (inFun) {
                        inFun = false
                        output.add(FunctionToken(function!!, subExpr))
                    } else {
                        output.add(ExpressionToken(subExpr))
                    }
                    inSub = false
                    continue
                }
                if (inSub) {
                    subExpr.add(token)
                } else {
                    output.add(token)
                }
            }

            return output
        }
    }
}