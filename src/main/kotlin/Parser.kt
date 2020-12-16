object Parser {
    operator fun invoke(tokens: List<Token>): RootNode = TokenParser(TokenSorter(tokens))//)

    object TokenSorter {
        operator fun invoke(tokens: List<Token>): List<Token> {
            if (tokens.isEmpty()) return listOf()
            val output = mutableListOf<Token>()

            val expr: MutableList<Token> = tokens.toMutableList()

            expr.replaceAll { when (it) {
                is ExpressionToken -> SortedExpressionToken(TokenSorter(it.expr))
                is FunctionToken -> SortedFunctionToken(it.function, TokenSorter(it.expr))
                else -> it
            }}

            var found: Token?
            found = expr.reversed().find { it is OperatorToken && (it.operator == Operator.PLUS || it.operator == Operator.MINUS) }
            if (found == null) found = expr.reversed().find { it is OperatorToken && (it.operator == Operator.TIMES || it.operator == Operator.DIV || it.operator == Operator.REM) }
            if (found == null) found = expr.reversed().find { it is OperatorToken && it.operator == Operator.POW }
            if (found == null) {
                found = expr.find { it is NumberToken || it is VarToken || it is SortedExpressionToken || it is SortedFunctionToken }
                return if (found == null) listOf()
                else listOf(found)
            }
            output.add(found)

            val functionSplit = expr.lastIndexOf(found)
            val rightString = expr.subList(functionSplit + 1, expr.size)
            val leftString = expr.subList(0, functionSplit)

            val rightTokens = TokenSorter(rightString)
            val leftTokens = TokenSorter(leftString)

            output.addAll(rightTokens)
            output.addAll(leftTokens)

            return output
        }
    }

    object TokenParser {
        operator fun invoke(tokens: List<Token>): RootNode {
            val tree = RootNode(null)

            val iterator = tokens.iterator()

            var parent: TreeNode = tree
            var right = true
            while (iterator.hasNext()) {
                val token = iterator.next()
                val node = nextNode(parent, token)
                when (parent) {
                    is RootNode -> parent.node = node
                    is OperatorNode -> if (right) parent.rightChild = node else parent.leftChild = node
                }
                when (node) {
                    is RootNode -> {
                        parent = node
                    }
                    is OperatorNode -> {
                        parent = node
                        right = true
                    }
                    is NumberNode, is VarNode, is ExpressionNode, is FunctionNode -> {
                        if (!right) {
                            do {
                                parent = parent.parent ?: return tree
                            } while (parent !is OperatorNode || parent.leftChild != null)
                        }
                        right = false
                    }
                }
            }

            return tree
        }

        private fun nextNode(parent: TreeNode, token: Token): TreeNode = when (token) {
            is SortedExpressionToken -> ExpressionNode(parent, TokenParser(token.expr))
            is SortedFunctionToken -> FunctionNode(parent, token.function, TokenParser(token.expr))
            is NumberToken -> NumberNode(parent, token.number)
            is VarToken -> VarNode(parent, token.name)
            is OperatorToken -> OperatorNode(parent, token.operator, null, null)
            else -> throw SyntaxError("Unexpected token: $token")
        }
    }
}