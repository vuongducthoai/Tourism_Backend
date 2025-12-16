package com.tourism.backend.util;

public class BankUntil {
    public static String getBankNameFromCode(String bankCode) {
        if (bankCode == null || bankCode.trim().isEmpty()) {
            return "Không xác định";
        }

        String code = bankCode.trim().toUpperCase();

        switch (code) {
            case "970436": case "VCB": case "VIETCOMBANK":
                return "VIETCOMBANK";

            case "970415": case "ICB": case "VIETINBANK":
                return "VIETINBANK";

            case "970418": case "BIDV": case "BID":
                return "BIDV";

            case "970405": case "VBA": case "AGRIBANK": case "VARB":
                return "AGRIBANK";

            case "970422": case "MB": case "MBBANK":
                return "MBBANK";

            case "970407": case "TCB": case "TECHCOMBANK":
                return "TECHCOMBANK";

            case "970416": case "ACB":
                return "ACB";

            case "970432": case "VPB": case "VPBANK":
                return "VPBANK";

            case "970423": case "TPB": case "TPBANK":
                return "TPBANK";

            case "970403": case "STB": case "SACOMBANK":
                return "SACOMBANK";

            case "970437": case "HDB": case "HDBANK":
                return "HDBANK";

            case "970441": case "VIB":
                return "VIB";

            case "970426": case "MSB": case "MARITIMEBANK":
                return "MARITIMEBANK";

            case "970448": case "OCB":
                return "OCB";

            case "970443": case "SHB":
                return "Ngân hàng Sài Gòn - Hà Nội (SHB)";

            case "970431": case "EIB": case "EXIMBANK":
                return "Ngân hàng Xuất Nhập Khẩu (Eximbank)";

            case "970440": case "SEAB": case "SEABANK":
                return "Ngân hàng Đông Nam Á (SeABank)";

            case "970449": case "LPB": case "LIENVIETPOSTBANK": case "LPBANK":
                return "Ngân hàng Lộc Phát (LPBank)";

            case "970454": case "VCCB": case "VIETCAPITALBANK": case "BV": case "BVBANK":
                return "Ngân hàng Bản Việt (BVBank)";

            case "970425": case "ABB": case "ABBANK":
                return "Ngân hàng An Bình (ABBank)";

            case "970428": case "NAB": case "NAMABANK":
                return "Ngân hàng Nam Á (Nam A Bank)";

            case "970427": case "VAB": case "VIETABANK":
                return "Ngân hàng Việt Á (VietA Bank)";

            case "970438": case "BVB": case "BAOVIETBANK":
                return "Ngân hàng Bảo Việt";

            case "970419": case "NCB":
                return "Ngân hàng Quốc Dân (NCB)";

            case "970430": case "PGB": case "PGBANK":
                return "Ngân hàng PGBank";

            case "970408": case "GPB": case "GPBANK":
                return "Ngân hàng Dầu Khí Toàn Cầu (GPBank)";

            case "970414": case "OJB": case "OCEANBANK":
                return "Ngân hàng Đại Dương (OceanBank)";

            case "970444": case "CBB": case "CBBANK":
                return "Ngân hàng Xây Dựng (CBBank)";

            case "970400": case "SGB": case "SAIGONBANK":
                return "Ngân hàng Sài Gòn Công Thương";

            case "970421": case "VRB":
                return "Ngân hàng Liên doanh Việt - Nga";

            case "970434": case "IVB": case "INDOVINABANK":
                return "Ngân hàng Indovina";

            case "970452": case "KLB": case "KIENLONGBANK":
                return "Ngân hàng Kiên Long";

            case "970429": case "SCB":
                return "Ngân hàng Sài Gòn (SCB)";

            case "970458":
            case "UOB": return "Ngân hàng UOB";

            case "970424":
            case "SHINHAN": case "SHBVN": return "Ngân hàng Shinhan Bank";

            case "970455":
            case "PUBLIC": case "PUBLICBANK": return "Ngân hàng Public Bank";

            case "970457":
            case "WOORI": case "WOORIBANK": return "Ngân hàng Woori Bank";

            case "971005": case "VIETTELMONEY": case "VTP": return "Viettel Money";
            case "971010": case "VNPTMONEY": return "VNPT Money";

            default:
                if (code.startsWith("9704")) {
                    return "Thẻ/TK Ngân hàng (NAPAS " + code + ")";
                }
                return "Ngân hàng " + code;
        }
    }
}
