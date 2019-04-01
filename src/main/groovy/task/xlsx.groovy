import groovy.sql.Sql
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook

def path = "C:\\Users\\Administrator\\Desktop\\work\\微信收款数据\\re.xlsx"

def prop = new Properties()
def s = "./test_db.properties"
def local="db.properties"
new File(s).withInputStream {
    stream->prop.load(stream)
}

println(prop)

read(path,prop)
def read(path,prop) {
    Sql sql = Sql.newInstance(prop)
    Workbook workbook = new XSSFWorkbook(new File(path))
    def sheet = workbook.getSheetAt(0)
    sheet.eachWithIndex { Row row, int i ->
        List<String> param = new LinkedList<>()
        boolean flag=false
        if (i > 0) {
            param.add(formatNo(i))//receiptCode
            param.add(row.getCell(1).getStringCellValue())//voucherNo
            param.add(row.getCell(0).getStringCellValue())//receiptDate
            param.add("10")//receiptType
            if (row.getCell(12) != null) {
                param.add(String.valueOf(row.getCell(12).getNumericCellValue()).replace(".0",""))
                flag = true
            }else {
                param.add("")//customerCode
            }
            param.add(row.getCell(11).getStringCellValue())//paymentName
            param.add(String.valueOf(row.getCell(6).getNumericCellValue()))//remitAmount
            param.add("0")//useAmount
            param.add("10001")//payeeName
            param.add("")//payeeAccount
            param.add("10")//paymentType
            param.add("20")//inputType
            param.add("1")//state
            flag ? param.add("2") : param.add("1")
            param.add("admin")//inputMan
            param.add(new Date())//inputDate
            param.add(new Date())//updateDate
            param.add("微信支付")
        }
        if (param.size() > 0) {
            println("param size:{}"+param.size())
            String insert = '''
       INSERT INTO `CashReceipt`(
        `receiptCode`,
        `voucherNo`,
        `receiptDate`,
        `receiptType`,
        `customerCode`,
        `paymentName`,
        `remitAmount`,
        `useAmount`,
        `payeeName`,
        `payeeAccount`,
        `paymentType`,
        `inputType`,
        `state`,
        `useState`,
        `inputMan`,
        `inputDate`,
        `updateDate`,
        `paymentBank`
        )
        VALUES
        (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)

    '''
            def insert1 = sql.executeInsert(insert, param)
            println(insert1)

        }
    }


}

def formatNo(int n) {
    String no = "RV_M20190329"
    return autoComplete(n, no)
}
def autoComplete(int n, String no) {
    int length = String.valueOf(n).length()
    switch (length) {
        case 1:
            return no + "00000" + n
        case 2:
            return no + "0000" + n
        case 3:
            return no + "000" + n
        case 4:
            return no + "00" + n
        case 5:
            return no + "0" + n
        default:
            return no + n
    }
}

