fun Token.print() {
    when (this) {
        is ExprStartToken -> print("Expression Start")
        is ExprEndToken -> print("Expression End")
        is ExpressionToken -> { print("Expression: "); printTokensNoNL(expr) }
        is FunctionToken -> { print("Function \"${function.name}\""); printTokensNoNL(expr) }
        is NumberToken -> print("Number: $number")
        is VarToken -> print("Var: $name")
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
        is FunctionNode -> {
            println("${before}Function ${function.name}")
            expr?.print("$before ")
        }
        is NumberNode -> println("$before$number")
        is VarNode -> println("$before$name")
        is OperatorNode -> {
            leftChild?.print("$before ")
            println("$before${operator.name}")
            rightChild?.print("$before ")
        }
    }
}

fun printTree(tree: ExpressionNode) {
    tree.print()
}