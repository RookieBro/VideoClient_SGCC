package com.telecomyt.videolibrary.bean;

/**
 * @author lbx
 * @date 2018/1/24.
 */

public class VideoParticipantBean {

    private boolean __hashCodeCalc;
    private boolean appshare;
    private boolean audio;
    private boolean canCallDirect;
    private boolean canControl;
    private boolean canJoinMeeting;
    private boolean canRecordMeeting;
    private String displayName;
    private String emailAddress;
    private String entityID;

    private String extension;
    private boolean isInMyContacts;
    private String ownerID;
    private String participantID;
    private String tenant;
    private boolean video;
    private boolean handRaised;

    private RoomMode roomMode;

    public boolean isHashCodeCalc() {
        return __hashCodeCalc;
    }

    public boolean isAppshare() {
        return appshare;
    }

    public boolean isAudio() {
        return audio;
    }

    public boolean isCanCallDirect() {
        return canCallDirect;
    }

    public boolean isCanControl() {
        return canControl;
    }

    public boolean isCanJoinMeeting() {
        return canJoinMeeting;
    }

    public boolean isCanRecordMeeting() {
        return canRecordMeeting;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getEntityID() {
        return entityID;
    }

    public String getExtension() {
        return extension;
    }

    public boolean isInMyContacts() {
        return isInMyContacts;
    }

    public String getOwnerID() {
        return ownerID;
    }

    public String getParticipantID() {
        return participantID;
    }

    public String getTenant() {
        return tenant;
    }

    public boolean isVideo() {
        return video;
    }

    public boolean isHandRaised() {
        return handRaised;
    }

    public RoomMode getRoomMode() {
        return roomMode;
    }

    public class RoomMode {
        private boolean __hashCodeCalc;
        private boolean hasModeratorPIN;
        private boolean hasPIN;
        private boolean isLocked;
        private String moderatorPIN;
        private String roomPIN;

        public boolean isHashCodeCalc() {
            return __hashCodeCalc;
        }

        public boolean isHasModeratorPIN() {
            return hasModeratorPIN;
        }

        public boolean isHasPIN() {
            return hasPIN;
        }

        public boolean isLocked() {
            return isLocked;
        }

        public String getModeratorPIN() {
            return moderatorPIN;
        }

        public String getRoomPIN() {
            return roomPIN;
        }

        @Override
        public String toString() {
            return "RoomMode{" +
                    "__hashCodeCalc=" + __hashCodeCalc +
                    ", hasModeratorPIN=" + hasModeratorPIN +
                    ", hasPIN=" + hasPIN +
                    ", isLocked=" + isLocked +
                    ", moderatorPIN='" + moderatorPIN + '\'' +
                    ", roomPIN='" + roomPIN + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "VideoParticipantBean{" +
                "__hashCodeCalc=" + __hashCodeCalc +
                ", appshare=" + appshare +
                ", audio=" + audio +
                ", canCallDirect=" + canCallDirect +
                ", canControl=" + canControl +
                ", canJoinMeeting=" + canJoinMeeting +
                ", canRecordMeeting=" + canRecordMeeting +
                ", displayName='" + displayName + '\'' +
                ", emailAddress='" + emailAddress + '\'' +
                ", entityID='" + entityID + '\'' +
                ", extension='" + extension + '\'' +
                ", isInMyContacts=" + isInMyContacts +
                ", ownerID='" + ownerID + '\'' +
                ", participantID='" + participantID + '\'' +
                ", tenant='" + tenant + '\'' +
                ", video=" + video +
                ", roomMode=" + roomMode +
                '}';
    }
}

