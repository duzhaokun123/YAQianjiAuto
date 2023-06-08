function parse(data, format, timestamp) {
    if (format != "json") return
    const json = JSON.parse(data)
    if (json.templateType != "S") return
    if (json.templateName != "基金每日收益提醒") return

    total = json.content
    total = total.substring(total.indexOf("∝") + 1, total.lastIndexOf("∝"))
    left = json.extraInfo.subCgyLeftValue
    left = left.substring(left.indexOf("∝") + 1, left.lastIndexOf("∝"))
    leftText = json.extraInfo.subCgyLeftKey
    middle = json.extraInfo.subCgyMiddleValue
    middle = middle.substring(middle.indexOf("∝") + 1, middle.lastIndexOf("∝"))
    middleText = json.extraInfo.subCgyMiddleKey
    right = json.extraInfo.subCgyRightValue
    right = right.substring(right.indexOf("∝") + 1, right.lastIndexOf("∝"))
    rightText = json.extraInfo.subCgyRightKey

    result = {}
    result.balance = parseFloat(total)
    if (result.balance < 0) {
        result.type = 1
    } else {
        result.type = 2
    }
    result.account = "余额宝"
    result.target = "基金"
    result.remark = leftText + ": " + left + " " + middleText + ": " + middle + " " + rightText + ": " + right
    result.extras = {}
    result.extras[leftText] = left
    result.extras[middleText] = middle
    result.extras[rightText] = right
    result.timestamp = timestamp
    return JSON.stringify(result)
}