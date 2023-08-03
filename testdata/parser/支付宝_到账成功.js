function parse(data, format, timestamp) {
    if (format != "json") return
    const json = JSON.parse(data)
    if (json.templateType != "BN") return
    if (json.title != "到账成功") return

    result = {}
    result.type = 2
    result.account = "支付宝"
    result.balance = parseFloat(json.content.money)
    json.content.content.forEach((item) => {
        if (item.title == "交易对象：") result.target = item.content
        if (item.title == "收款内容：") result.remark = item.content
    })
    result.extras = {
        "sceneName": json.extraInfo.sceneExt2.sceneName
    }
    result.timestamp = timestamp
    return JSON.stringify(result)
}