package com.moim.backend.domain.space.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class KakaoMapDetailDto {
    private boolean isMapUser;
    private boolean isExist;
    private BasicInfo basicInfo;
    private BlogReview blogReview;
    private Comment comment;
    private FindWay findway;
    private PlaceOwnerInfos placeOwnerInfos;
    private MenuInfo menuInfo;
    private Photo photo;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class BasicInfo {
        private long cid;
        private String placenamefull;
        private String mainphotourl;
        private String phonenum;
        private Address address;
        private String homepage;
        private String homepagenoprotocol;
        private long wpointx;
        private long wpointy;
        private RoadView roadview;
        private Category category;
        private Map<String, Long> feedback;
        private OpenHour openHour;
        private OperationInfo operationInfo;
        private FacilityInfo facilityInfo;
        private List<String> tags;
        private Source source;
        private boolean isStation;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Address {
        private NewAddr newaddr;
        private Region region;
        private String addrbunho;
        private String addrdetail;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class NewAddr {
        private String newaddrfull;
        private String bsizonno;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Region {
        private String name3;
        private String fullname;
        private String newaddrfullname;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Category {
        private String cateid;
        private String catename;
        private String cate1Name;
        private String fullCateIds;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static
    public class FacilityInfo {
        private String wifi;
        private String pet;
        private String parking;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static
    public class OpenHour {
        private List<PeriodList> periodList;
        private Realtime realtime;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static
    public class PeriodList {
        private String periodName;
        private List<TimeList> timeList;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class TimeList {
        private String timeName;
        private String timeSE;
        private String dayOfWeek;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TimeList timeList = (TimeList) o;
            return Objects.equals(getTimeName(), timeList.getTimeName()) && Objects.equals(getTimeSE(), timeList.getTimeSE()) && Objects.equals(getDayOfWeek(), timeList.getDayOfWeek());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getTimeName(), getTimeSE(), getDayOfWeek());
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Realtime {
        private String holiday;
        private String breaktime;
        private String open;
        private String moreOpenOffInfoExists;
        private String datetime;
        private PeriodList currentPeriod;
        private String closedToday;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class OperationInfo {
        private String appointment;
        private String delivery;
        private String pagekage;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class RoadView {
        private long panoid;
        private long tilt;
        private double pan;
        private long wphotox;
        private long wphotoy;
        private long rvlevel;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    static
    public class Source {
        private String date;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class BlogReview {
        private String placenamefull;
        private long moreId;
        private long blogrvwcnt;
        private List<BlogReviewList> list;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class BlogReviewList {
        private String blogname;
        private String blogurl;
        private String contents;
        private String outlink;
        private String date;
        private String reviewid;
        private String title;
        private List<PurplePhotoList> photoList;
        private boolean isMy;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class PurplePhotoList {
        private String orgurl;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Comment {
        private String placenamefull;
        private long kamapComntcnt;
        private long scoresum;
        private long scorecnt;
        private List<CommentList> list;
        private List<StrengthCount> strengthCounts;
        private boolean hasNext;
        private String reviewWriteBlocked;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class CommentList {
        private String commentid;
        private String contents;
        private long point;
        private String username;
        private String profile;
        private String profileStatus;
        private long photoCnt;
        private long likeCnt;
        private String thumbnail;
        private String kakaoMapUserId;
        private List<FluffyPhotoList> photoList;
        private OwnerReply ownerReply;
        private long userCommentCount;
        private double userCommentAverageScore;
        private boolean myStorePick;
        private String date;
        private boolean isMy;
        private boolean isBlock;
        private boolean isEditable;
        private boolean isMyLike;
        private List<Strength> strengths;
    }

    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.NONE)
    @AllArgsConstructor
    @Data
    public static class OwnerReply {
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class FluffyPhotoList {
        private String url;
        private boolean near;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Strength {
        private long id;
        private String name;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class StrengthCount {
        private long id;
        private String name;
        private long count;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Subway {
        private String stationSimpleName;
        private String stationId;
        private String exitNum;
        private long toExitDistance;
        private List<SubwayList> subwayList;
        private long toExitMinute;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class SubwayList {
        private String subwayId;
        private String subwayName;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class MenuInfo {
        private long menucount;
        private List<MenuList> menuList;
        private String productyn;
        private long menuboardphotocount;
        private String timeexp;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MenuInfo menuInfo = (MenuInfo) o;
            return getMenucount() == menuInfo.getMenucount() && getMenuboardphotocount() == menuInfo.getMenuboardphotocount()  && Objects.equals(getProductyn(), menuInfo.getProductyn()) && Objects.equals(getTimeexp(), menuInfo.getTimeexp());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getMenucount(), getProductyn(), getMenuboardphotocount(), getTimeexp());
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class MenuList {
        private String price;
        private boolean recommend;
        private String menu;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Photo {
        private List<PhotoPhotoList> photoList;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class PhotoPhotoList {
        private long photoCount;
        private String categoryName;
        private List<PhotoListList> list;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class PhotoListList {
        private String photoid;
        private String orgurl;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class PlaceOwnerInfos {
        private String status;
        private String loginUserRelation;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class FindWay {
        private long x;
        private long y;
        private List<Subway> subway;
        private List<BusStop> busstop;
        private boolean busDirectionCheck;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class BusStop {
        private String busStopId;
        private String busStopName;
        private String busStopDisplayId;
        private long toBusstopDistance;
        private long wpointx;
        private long wpointy;
        private List<BusInfo> busInfo;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class BusInfo {
        private String busType;
        private String busTypeCode;
        private List<BusList> busList;
        private String busNames;
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class BusList {
        private String busId;
        private String busName;
    }

}
