function parse(data, format, timestamp) {
    if (format != "json") return
    const json = JSON.parse(data)
    if (json.templateType != "BN") return
    if (json.title != "资金到账通知") return

    result = {}
    result.type = 2
    result.account = "余额宝"
    result.balance = parseFloat(json.content.money)
    json.content.content.forEach((item) => {
        if (item.title == "付款方：") result.target = item.content
        if (item.title == "到账时间：") result.timestamp = Date.parse(item.content)
    })
    result.extras = {}
    result.remark = ""
    return JSON.stringify(result)
}