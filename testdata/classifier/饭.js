function classify(parsedData) {
    json = JSON.parse(parsedData)
    if (json.type != 1) return
    patt = /^西安石油大学$|^德克士|^迈德思客|麦当劳|^饿了么$|^肯德基|^必胜客/i
    if (patt.test(json.target) == false) return
    result = {
        "category": "三餐",
    }

    expenseHour = new Date(json.timestamp).getHours()
    if (expenseHour >= 6 && expenseHour <= 9) {
        result.subcategory = "早餐"
    } else if (expenseHour >= 11 && expenseHour <= 13) {
        result.subcategory = "午餐"
    } else if (expenseHour >= 17 && expenseHour <= 19) {
        result.subcategory = "晚餐"
    } else {
        result.subcategory = "夜宵"
    }
    return JSON.stringify(result)
}