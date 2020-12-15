object Parser {
    operator fun invoke(tokens: List<Token>): ExpressionNode = TokenParser(TokenSorter(listOf()))

    object TokenSorter {
        operator fun invoke(tokens: List<Token>): List<Token> {
            if (tokens.isEmpty()) return listOf()
            val output = mutableListOf<Token>()

            var found = tokens.reversed().find { it is OperatorToken && (it.operator == Operator.PLUS || it.operator == Operator.MINUS) }
            if (found == null) found = tokens.reversed().find { it is OperatorToken && (it.operator == Operator.TIMES || it.operator == Operator.DIV || it.operator == Operator.REM) }
            if (found == null) found = tokens.reversed().find { it is OperatorToken && it.operator == Operator.POW }
            if (found == null) {
                found = tokens.find { it is NumberToken }
                return if (found == null) listOf()
                else listOf(found)
            }
            output.add(found)

            val functionSplit = tokens.lastIndexOf(found)
            val rightString = tokens.subList(functionSplit + 1, tokens.size)
            val leftString = tokens.subList(0, functionSplit)

            output.addAll(TokenSorter(rightString))
            output.addAll(TokenSorter(leftString))

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
                val node = nextNode(parent, iterator.next())
                when (parent) {
                    is ExpressionNode -> parent.rootNode = node
                    is OperatorNode -> if (right) parent.rightChild = node else parent.leftChild = node
                }
                when (node) {
                    is OperatorNode -> {
                        parent = node
                        right = true
                    }
                    is NumberNode -> {
                        if (!right) {
                            do {
                                parent = parent.parent ?: return tree
                            } while (parent !is OperatorNode)
                        }
                        right = false
                    }
                }
            }

            return tree
        }

        fun nextNode(parent: TreeNode, token: Token): TreeNode = when (token) {
            is NumberToken -> NumberNode(parent, token.number)
            is OperatorToken -> OperatorNode(parent, token.operator, null, null)
            else -> throw SyntaxError("Unexpected token: $token")
        }
    }
}