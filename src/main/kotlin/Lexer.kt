object Lexer {
    operator fun invoke(function: String): List<Token> = TokenConcat(Tokenizer(function))

    object Tokenizer {
        operator fun invoke(function: String): List<Token> {
            val tokens = mutableListOf<Token>()

            var lastToken: Token = ExpressionToken(true)

            tokens.add(ExpressionToken(true))
            val expressionStarts = 1

            val iterator = function.trimAll().iterator()
            for (c in iterator) {
                val token: Token = when (lastToken) {
                    is ExpressionToken, is OperatorToken -> tokenOf(c, TokenType.NUMBER, TokenType.SIGN)
                    is NumberToken -> tokenOf(c, TokenType.NUMBER, TokenType.OPERATOR)
                    is SignToken -> tokenOf(c, TokenType.SIGN, TokenType.NUMBER)
                    else -> null
                } ?: throw SyntaxError("Unexpected token '$c'")
                lastToken = token
                tokens.add(token)
            }
            repeat(expressionStarts) {
                tokens.add(ExpressionToken(false))
            }

            return tokens
        }

        private fun tokenOf(c: Char, vararg accepted: TokenType): Token? {
            if (accepted.contains(TokenType.NUMBER)) {
                if (c in "1234567890") return NumberToken(c.toString().toInt())
            }
            if (accepted.contains(TokenType.OPERATOR)) {
                if (c in "+-*/%^") return OperatorToken(Operator.ofChar(c))
            }
            if (accepted.contains(TokenType.SIGN)) {
                if (c in "+-") return SignToken(c == '+')
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
                if (token is SignToken) {
                    val newToken = NumberToken(if (token.sign) 1 else -1)
                    signConcat@do {
                        val next = tokens[i + 1]
                        if (next is SignToken) {
                            newToken.number = newToken.number * (if (next.sign) 1 else -1)
                        } else if (next is NumberToken) {
                            newToken.number = newToken.number * next.number
                            while (++i < tokens.size - 1) {
                                val nextNumber = tokens[i + 1]
                                if (nextNumber is NumberToken) {
                                    newToken.number = newToken.number * 10 + nextNumber.number
                                } else break@signConcat
                            }
                            output.add(newToken)
                        } else break@signConcat
                    } while (++i < tokens.size - 1)
                    output.add(newToken)
                } else if (token is NumberToken) {
                    val newToken = NumberToken(token.number)
                    numberConcat@do {
                        val next = tokens[i + 1]
                        if (next is NumberToken) {
                            newToken.number = newToken.number * 10 + next.number
                        } else break@numberConcat
                    } while (++i < tokens.size - 1)
                    output.add(newToken)
                } else {
                    output.add(token)
                }
                ++i
            }

            return output
        }
    }
}