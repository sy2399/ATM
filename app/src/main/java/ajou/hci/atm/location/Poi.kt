package ajou.hci.atm.location

data class Poi (val name: String?, // 시설물 명칭
                 val upperAddrName: String?, // 표출 주소 대분류명
                 val middleAddrName: String?, // 표출 주소 중분류명
                 val lowerAddrName: String?, // 표출 주소 소분류명
                 val detailAddrName: String?, // 표출 주소 세분류명
                 //val roadName: String?, // 도로명
                 val radius: String? // 거리 (km)
)
data class Pois (val poi: List<Poi>?)

data class SearchPoiInfo(val totalCount: Int, val pois: Pois)

data class Response(val searchPoiInfo: SearchPoiInfo)