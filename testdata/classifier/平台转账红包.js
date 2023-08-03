function classify(parsedData) {
    json = JSON.parse(parsedData)
    if (json.type != 2) return
    result = {
        "category": "转账红包",
    }
    if (/^淘宝.*com$/.test(json.target)) return JSON.stringify(result)
    if ("支付宝红包" == json.extras.sceneName) return JSON.stringify(result)
}