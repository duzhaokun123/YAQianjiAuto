function parse(data, format, timestamp) {
    return JSON.stringify({
        type: 1,
        account: "account",
        balance: 10.0,
        target: "target",
        remark: "remark",
        timestamp: timestamp,
        extras: {
            aaa: "aaa",
            bbb: "bbb"
        }
    })
}