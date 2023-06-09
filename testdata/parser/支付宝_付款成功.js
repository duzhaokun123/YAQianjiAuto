function parse(data, format, timestamp) {
    if (format != "json") return
    const json = JSON.parse(data)
    if (json.templateType != "BN") return
    if (json.title != "付款成功") return

    result = {}
    result.type = 1
    result.balance = parseFloat(json.content.money)
    json.content.content.forEach((item) => {
        if (item.title == "付款方式：") result.account = item.content
        if (item.title == "交易对象：") result.target = item.content
    })
    sceneName = json.extraInfo.sceneExt2.sceneName
    if (sceneName == "天猫") {
        result.remark = sceneName
    } else {
        result.remark = ""
    }
    result.extras = {
        "sceneName": sceneName,
    }
    result.timestamp = timestamp
    return JSON.stringify(result)
}