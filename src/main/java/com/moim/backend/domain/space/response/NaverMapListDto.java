package com.moim.backend.domain.space.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@NoArgsConstructor
public class NaverMapListDto {

    @JsonProperty("result")
    private Result result;

    public Result getResult() {
        return result;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Result {
        @JsonProperty("place")
        private Place place;
        @JsonProperty("metaInfo")
        private MetaInfo metaInfo;
        @JsonProperty("type")
        private String type;

        public Place getPlace() {
            return place;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Place {
        @JsonProperty("list")
        private List<placeList> list;

        public List<placeList> getList() {
            return list;
        }

        @JsonProperty("containAdultContents")
        private boolean containAdultContents;
        @JsonProperty("isAdultKeyword")
        private boolean isAdultKeyword;
        @JsonProperty("hasPollingPlace")
        private boolean hasPollingPlace;
        @JsonProperty("isSiteSortAvailable")
        private boolean isSiteSortAvailable;
        @JsonProperty("filters")
        private Filters filters;
        @JsonProperty("options")
        private Options options;
        @JsonProperty("feedback")
        private List<String> feedback;
        @JsonProperty("boundary")
        private List<String> boundary;
        @JsonProperty("totalCount")
        private int totalCount;
        @JsonProperty("page")
        private int page;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class placeList {
        @JsonProperty("microReview")
        private List<String> microReview;
        @JsonProperty("markerId")
        private String markerId;
        @JsonProperty("markerSelected")
        private String markerSelected;
        @JsonProperty("marker")
        private String marker;
        @JsonProperty("distance")
        private String distance;
        @JsonProperty("carWash")
        private String carWash;
        @JsonProperty("hasNPay")
        private boolean hasNPay;
        @JsonProperty("hasBroadcastInfo")
        private boolean hasBroadcastInfo;
        @JsonProperty("reservation")
        private Reservation reservation;
        @JsonProperty("reservationLabel")
        private ReservationLabel reservationLabel;
        @JsonProperty("hasNaverSmartOrder")
        private boolean hasNaverSmartOrder;
        @JsonProperty("naverBookingUrl")
        private String naverBookingUrl;
        @JsonProperty("hasNaverBooking")
        private boolean hasNaverBooking;
        @JsonProperty("menuExist")
        private String menuExist;
        @JsonProperty("hasCardBenefit")
        private boolean hasCardBenefit;

        @JsonProperty("menuInfo")
        private String menuInfo;

        @JsonProperty("bizhourInfo")
        private String bizhourInfo;
        @JsonProperty("isPollingPlace")
        private boolean isPollingPlace;
        @JsonProperty("description")
        private String description;
        @JsonProperty("homePage")
        private String homePage;
        @JsonProperty("poiInfo")
        private PoiInfo poiInfo;
        @JsonProperty("streetPanorama")
        private StreetPanorama streetPanorama;
        @JsonProperty("isAdultBusiness")
        private boolean isAdultBusiness;
        @JsonProperty("itemLevel")
        private String itemLevel;
        @JsonProperty("y")
        private String y;
        @JsonProperty("x")
        private String x;

        public String getX() {
            return x;
        }

        public String getY() {
            return y;
        }

        @JsonProperty("posExact")
        private String posExact;
        @JsonProperty("isSite")
        private String isSite;
        @JsonProperty("type")
        private String type;
        @JsonProperty("thumUrls")
        private List<String> thumUrls;
        @JsonProperty("thumUrl")
        private String thumUrl;
        @JsonProperty("coupon")
        private String coupon;
        @JsonProperty("ktCallMd")
        private String ktCallMd;
        @JsonProperty("placeReviewCount")
        private int placeReviewCount;
        @JsonProperty("reviewCount")
        private int reviewCount;
        @JsonProperty("context")
        private List<String> context;
        @JsonProperty("telDisplay")
        private String telDisplay;
        @JsonProperty("display")
        private String display;
        @JsonProperty("roadAddress")
        private String roadAddress;
        @JsonProperty("address")
        private String address;
        @JsonProperty("businessStatus")
        private BusinessStatus businessStatus;
        @JsonProperty("rcode")
        private String rcode;
        @JsonProperty("categoryPath")
        private java.util.List<List<String>> categoryPath;
        @JsonProperty("category")
        private List<String> category;
        @JsonProperty("ppc")
        private String ppc;
        @JsonProperty("virtualTelDisplay")
        private String virtualTelDisplay;
        @JsonProperty("virtualTel")
        private String virtualTel;
        @JsonProperty("isCallLink")
        private boolean isCallLink;
        @JsonProperty("tel")
        private String tel;
        @JsonProperty("name")
        private String name;
        @JsonProperty("id")
        private String id;
        @JsonProperty("rank")
        private String rank;
        @JsonProperty("index")
        private String index;

        public Optional<String> getMenuInfoOptional() {
            return Optional.ofNullable(menuInfo);
        }

        public String getName() {
            return name;
        }

        public String getThumUrl() {
            return thumUrl;
        }

        public List<String> getThumUrls() {
            return thumUrls;
        }

        public BusinessStatus getBusinessStatus() {
            return businessStatus;
        }

        public String getTel() {
            return tel;
        }

        public String getRoadAddress() {
            return roadAddress;
        }

        public String getBizhourInfo() {
            return bizhourInfo;
        }

        public String getHomePage() {
            return homePage;
        }

        public List<String> getCategory() {
            return category;
        }

        public Optional<StreetPanorama> getStreetPanorama() {
            return Optional.ofNullable(streetPanorama);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Reservation {
        @JsonProperty("benefit")
        private String benefit;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReservationLabel {
        @JsonProperty("takeout")
        private boolean takeout;
        @JsonProperty("table")
        private boolean table;
        @JsonProperty("preOrder")
        private boolean preOrder;
        @JsonProperty("standard")
        private boolean standard;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class PoiInfo {
        @JsonProperty("hasPolygon")
        private boolean hasPolygon;
        @JsonProperty("hasLand")
        private boolean hasLand;
        @JsonProperty("hasRoad")
        private boolean hasRoad;
        @JsonProperty("road")
        private Road road;
        @JsonProperty("hasRelation")
        private boolean hasRelation;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Road {
        @JsonProperty("poiShapeType")
        private String poiShapeType;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class StreetPanorama {
        @JsonProperty("fov")
        private String fov;
        @JsonProperty("lat")
        private String lat;
        @JsonProperty("lng")
        private String lng;
        @JsonProperty("tilt")
        private String tilt;
        @JsonProperty("pan")
        private String pan;
        @JsonProperty("id")
        private String id;

        public Optional<String> getLng() {
            return Optional.ofNullable(lng);
        }

        public Optional<String> getLat() {
            return Optional.ofNullable(lat);
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class BusinessStatus {
        @JsonProperty("lastOrder")
        private String lastOrder;
        @JsonProperty("breakTime")
        private String breakTime;
        @JsonProperty("businessHours")
        private String businessHours;
        @JsonProperty("status")
        private Status status;
        @JsonProperty("requestTime")
        private String requestTime;

        public Status getStatus() {
            return status;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Status {
        @JsonProperty("detailInfo")
        private String detailInfo;
        @JsonProperty("description")
        private String description;
        @JsonProperty("emphasis")
        private boolean emphasis;
        @JsonProperty("text")
        private String text;
        @JsonProperty("code")
        private int code;

        public String getDetailInfo() {
            return detailInfo;
        }
    }

    public static class Filters {
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class Options {
        @JsonProperty("rank")
        private String rank;
        @JsonProperty("toggleLocation")
        private String toggleLocation;
        @JsonProperty("sort")
        private String sort;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class MetaInfo {
        @JsonProperty("displayCorrectAnswer")
        private boolean displayCorrectAnswer;
        @JsonProperty("searchedQuery")
        private String searchedQuery;
        @JsonProperty("rcode")
        private String rcode;
        @JsonProperty("pageId")
        private String pageId;
    }
}
