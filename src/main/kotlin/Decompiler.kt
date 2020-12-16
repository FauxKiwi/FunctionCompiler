object Decompiler {
    operator fun invoke(tree: ExpressionNode): String = DeLexer(DeParser(tree))

    object DeParser {
        operator fun invoke(tree: TreeNode): List<Token> {
            val output = mutableListOf<Token>()

            when (tree) {
                is ExpressionNode -> output.add(ExpressionRepresentToken(DeParser(tree.rootNode!!)))
                is NumberNode -> output.add(NumberToken(tree.number))
                is OperatorNode -> {
                    output.addAll(DeParser(tree.leftChild!!))
                    output.add(OperatorToken(tree.operator))
                    output.addAll(DeParser(tree.rightChild!!))
                }
            }

            return output
        }
    }

    object DeLexer {
        operator fun invoke(tokens: List<Token>): String {
            val output = StringBuilder()

            for (token in tokens) {
                output.append(when (token) {
                    is ExprStartToken -> '('
                    is ExprEndToken -> ')'
                    is ExpressionRepresentToken -> "(${DeLexer(token.expr)})"
                    is NumberToken -> token.number
                    is OperatorToken -> token.operator.toString()
                    is SignToken -> if (token.sign) '+' else '-'
                    else -> ""
                })
            }

            return output.toString()
        }
    }
}