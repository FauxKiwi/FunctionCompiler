fun main() {
    /*val function = "11-+--5^3/5"
    val tokenized = Lexer.Tokenizer(function)
    printTokens(tokenized)
    val lexed = Lexer.TokenConcat(tokenized)
    printTokens(lexed)

    val tokens = Parser.TokenSorter(lexed)
    printTokens(tokens)
    val tree = Parser.TokenParser(tokens)
    printTree(tree)
    val result = Calculator(tree)
    println(result)

    println(Calculator("1-+5^3/5"))*/

    val tokens = Lexer("(x-6)^3/5")
    printTokens(tokens)
    println(" --- ")
    val sorted = Parser.TokenSorter(tokens)
    printTokens(sorted)
    println(" --- ")
    val resolved = Parser.ExpressionResolver(sorted)
    printTokens(resolved)
    println(" --- ")
    val tree = Parser.TokenParser(resolved)
    printTree(tree)
    println(" --- ")
    println(Calculator(tree, Pair("x", 1.0)))
    println()
    println(" ------- ")
    println()
    val dep = Decompiler.DeParser(tree)
    printTokens(dep)
    println(" --- ")
    val del = Decompiler.DeLexer(dep)
    println(del)

    /*val tree = Parser.TokenParser(listOf(
        OperatorToken(Operator.DIV),
        NumberToken(5),
        OperatorToken(Operator.POW),
        NumberToken(3),
        ExprEndToken(),
        OperatorToken(Operator.MINUS),
        NumberToken(6),
        NumberToken(1),
        ExprStartToken(),
    ))
    printTree(tree)
    println(Calculator(tree))

    println(Calculator(
        ExpressionNode(null,
            OperatorNode(null, Operator.DIV,
                OperatorNode(null, Operator.POW,
                    ExpressionNode(null,
                        OperatorNode(null, Operator.MINUS,
                            NumberNode(null, 1),
                            NumberNode(null, 6)
                        )
                    ),
                    NumberNode(null, 3)
                ),
                NumberNode(null, 5)
            )
        )
    ))*/
}