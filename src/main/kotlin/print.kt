fun Token.print() {
    when (this) {
        is ExprStartToken -> print("Expression Start")
        is ExprEndToken -> print("Expression End")
        is ExpressionRepresentToken -> { print("Expression: "); printTokensNoNL(expr) }
        is NumberToken -> print("Number: $number")
        is OperatorToken -> print("Operator: ${operator.name}")
        is SignToken -> print("Sign: ${if (sign) '+' else '-'}")
    }
}

fun printTokens(tokens: List<Token>) {
    print('[')
    val iterator = tokens.iterator()
    for (token in iterator) {
        token.print()
        if (iterator.hasNext()) print(", ")
    }
    print(']')
    println()
}

fun printTokensNoNL(tokens: List<Token>) {
    print('[')
    val iterator = tokens.iterator()
    for (token in iterator) {
        token.print()
        if (iterator.hasNext()) print(", ")
    }
    print(']')
}

fun TreeNode.print(before: String = "") {
    when (this) {
        is ExpressionNode -> {
            println("${before}Expression")
            rootNode?.print("$before ")
        }
        is OperatorNode -> {
            leftChild?.print("$before ")
            println("$before${operator.name}")
            rightChild?.print("$before ")
        }
        is NumberNode -> println("$before$number")
    }
}

fun printTree(tree: ExpressionNode) {
    tree.print()
}