function parse(data, format, timestamp) {
    if (format != "json") return
    const json = JSON.parse(data)
    if (json.templateType != "BN") return
    if (json.title != "自动扣款成功") return
    result = {
        remark: "",
        account: "",
        target: "",
    }
    result.type = 1
    result.balance = parseFloat(json.content.money)
    json.content.content.forEach((item) => {
        if (item.title == "扣款说明：") result.remark = item.content
        if (item.title == "付款方式：") result.account = item.content
        if (item.title == "交易对象：") result.target = item.content
    })
    result.extras = {}
    result.timestamp = timestamp
    return JSON.stringify(result)
}