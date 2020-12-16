object Parser {
    operator fun invoke(tokens: List<Token>): ExpressionNode = TokenParser(ExpressionResolver(TokenSorter(tokens)))

    object TokenSorter {
        operator fun invoke(tokens: List<Token>): List<Token> {
            if (tokens.isEmpty()) return listOf()
            val output = mutableListOf<Token>()

            var expr: List<Token> = tokens

            val exprEnd = expr.indexOfLast { it is ExprEndToken }
            if (exprEnd >= 0) {
                val exprStart = expr.indexOfFirst { it is ExprStartToken }
                if (exprStart < 0) throw SyntaxError("Closed bracket has not been opened")
                val subExpr = expr.subList(exprStart + 1, exprEnd)
                val leftOfSubExpr = if (exprStart > 0) expr.subList(0, exprStart - 1) else listOf()
                val rightOfSubExpr = if (exprEnd < expr.size - 1) expr.subList(exprEnd + 1, expr.size) else listOf()
                expr = leftOfSubExpr + ExpressionRepresentToken(TokenSorter(subExpr)) + rightOfSubExpr
            }

            var found: Token?
            found = expr.reversed().find { it is OperatorToken && (it.operator == Operator.PLUS || it.operator == Operator.MINUS) }
            if (found == null) found = expr.reversed().find { it is OperatorToken && (it.operator == Operator.TIMES || it.operator == Operator.DIV || it.operator == Operator.REM) }
            if (found == null) found = expr.reversed().find { it is OperatorToken && it.operator == Operator.POW }
            if (found == null) {
                found = expr.find { it is NumberToken || it is ExpressionRepresentToken }
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

    object ExpressionResolver {
        operator fun invoke(tokens: List<Token>): List<Token> {
            val output = mutableListOf<Token>()

            val iterator = tokens.iterator()

            for (token in iterator) {
                if (token is ExpressionRepresentToken) {
                    output.add(ExprEndToken())
                    output.addAll(token.expr)
                    output.add(ExprStartToken())
                } else {
                    output.add(token)
                }
            }

            return output
        }
    }

    object TokenParser {
        operator fun invoke(tokens: List<Token>): ExpressionNode {
            val tree = ExpressionNode(null, null)

            val iterator = tokens.iterator()

            var parent: TreeNode = tree
            var right = true
            while (iterator.hasNext()) {
                val token = iterator.next()
                if (token is ExprStartToken) {
                    while (parent !is ExpressionNode) {
                        parent = parent.parent ?: return tree
                    }
                    parent = parent.parent ?: return tree
                }
                val node = nextNode(parent, token)
                when (parent) {
                    is ExpressionNode -> parent.rootNode = node
                    is OperatorNode -> if (right) parent.rightChild = node else parent.leftChild = node
                }
                when (node) {
                    is ExpressionNode -> {
                        parent = node
                    }
                    is OperatorNode -> {
                        parent = node
                        right = true
                    }
                    is NumberNode -> {
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

        fun nextNode(parent: TreeNode, token: Token): TreeNode = when (token) {
            is ExprEndToken -> ExpressionNode(parent, null)
            is NumberToken -> NumberNode(parent, token.number)
            is OperatorToken -> OperatorNode(parent, token.operator, null, null)
            else -> throw SyntaxError("Unexpected token: $token")
        }
    }
}