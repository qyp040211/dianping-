let commonURL = '/api'
export const util = {
    commonURL,
    getUrlParam(name) {
        let reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)", "i");
        let r = window.location.search.substr(1).match(reg);
        if (r != null) {
            return decodeURI(r[2]);
        }
        return "";
    },
    /**
     * 格式化价格
     * @param {string|number} val - 输入的价格值
     * @param {boolean} [toCents=false] - 是否转换为分（整数）
     * @returns {string|number|null} 格式化后的价格
     */
    formatPrice(val, toCents = false) {
    // 处理空值
    if (val === null || val === undefined || val === "") {
        return null;
    }

    // 统一转为字符串处理
    let strVal = typeof val === 'number' ? val.toString() : val;

    // 验证是否为有效数字
    if (!/^-?\d*\.?\d*$/.test(strVal)) {
        return null;
    }

    // 处理负号
    const isNegative = strVal.startsWith('-');
    strVal = strVal.replace('-', '');

    // 分割整数和小数部分
    const [integerPart, decimalPart = ''] = strVal.split('.');

    if (toCents) {
        // 转换为分（整数）
        const cents = decimalPart.length >= 2
            ? decimalPart.substring(0, 2)
            : decimalPart + '0'.repeat(2 - decimalPart.length);

        const totalCents = parseInt(integerPart + cents, 10);
        return isNegative ? -totalCents : totalCents;
    } else {
        // 格式化为标准价格字符串（两位小数）
        const formattedDecimal = decimalPart.length >= 2
            ? decimalPart.substring(0, 2)
            : decimalPart + '0'.repeat(2 - decimalPart.length);

        return (isNegative ? '-' : '') + integerPart + '.' + formattedDecimal;
    }
}}