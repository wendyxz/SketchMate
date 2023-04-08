package wb.helper

fun processJsonString(jsonString: String): String {
    var depth = -1
    var result = ""
    for (char in jsonString) {
        when (char) {
            '{' -> {
                depth++
                result += char
            }
            '}' -> {
                depth--
                result += char
            }
            '"' -> {
                var tmp = ""
                if (depth == 1) {
                    tmp = "\\"
                } else if (depth > 1) {
                    tmp = "\\\\\\"
                }
                val quoteReplacement = tmp + char
                result += quoteReplacement
            }
            else -> {
                result += char
            }
        }
    }
    return result
}

fun removeDoubleQuotes(str: String): String {
    if (str.startsWith("\"") && str.endsWith("\"")) {
        return str.substring(1, str.length - 1)
    }
    return str
}
