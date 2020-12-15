fun main() {
    val function = "2^5"
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
}