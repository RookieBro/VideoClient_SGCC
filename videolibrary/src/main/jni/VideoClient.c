#include <jni.h>
#include <stdio.h>
#include <string.h>
#include "VidyoClient.h"
#include "include/AndroidDebug.h"

#include <sys/select.h>
#include <unistd.h>
#include <sys/socket.h>
#include <pthread.h>
#include <signal.h>
#include <sys/wait.h>
#include <android/log.h>
#include <sys/types.h>
#include <sys/un.h>
#include <errno.h>
#include <stdlib.h>
#define JNI_FALSE 0
#define JNI_TRUE 1

jobject applicationJniObj = 0;
JavaVM *global_vm = 0;
static VidyoBool joinStatus = 0;
int x;
int y;
static VidyoBool allVideoDisabled = 0;

void SampleSwitchCamera(const char *name);

void SampleStartConference();

void SampleEndConference();

void SampleLoginSuccessful();

void LibraryStarted();

// Callback for out-events from VidyoClient
#define PRINT_EVENT(X) if(event==X) LOGI("GuiOnOutEvent recieved %s", #X);

static JNIEnv *getJniEnv(jboolean *isAttached) {
    int status;
    JNIEnv *env;
    *isAttached = 0;

    status = (*global_vm)->GetEnv(global_vm, (void **) &env, JNI_VERSION_1_4);
    if (status < 0) {
        //LOGE("getJavaEnv: Failed to get Java VM");
        status = (*global_vm)->AttachCurrentThread(global_vm, &env, NULL);
        if (status < 0) {
            LOGE("getJavaEnv: Failed to get Attach Java VM");
            return NULL;
        }
        //LOGE("getJavaEnv: Attaching to Java VM");
        *isAttached = 1;
    }

    return env;
}

static jmethodID getApplicationJniMethodId(JNIEnv *env, jobject obj, const char *methodName, const char *methodSignature) {
    jmethodID mid;
    jclass appClass;

    appClass = (*env)->GetObjectClass(env, obj);
    if (!appClass) {
        LOGE("getApplicationJniMethodId - getApplicationJniMethodId: Failed to get applicationJni obj class");
        return NULL;
    }

    mid = (*env)->GetMethodID(env, appClass, methodName, methodSignature);
    if (mid == NULL) {
        LOGE("getApplicationJniMethodId - getApplicationJniMethodId: Failed to get %s method",
             methodName);
        return NULL;
    }

    return mid;
}

void ComingMsgG(const char *uri,const char *name,const char *msg) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring jsuri;
    jstring jsname;
    jstring jsmsg;
    LOGE("ComingMsgGCallback Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "ComingMsgGCallback", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
    LOGE("ComingMsgGCallback center");

    if (mid == NULL)
        goto FAIL1;

    jsuri = (*env)->NewStringUTF(env, uri);
    jsname = (*env)->NewStringUTF(env, name);
    jsmsg = (*env)->NewStringUTF(env, msg);
    (*env)->CallVoidMethod(env, applicationJniObj, mid, jsuri,jsname,jsmsg);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("ComingMsgGCallback End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("ComingMsgGCallback FAILED");
    return;
}

void EndingCall() {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("EndingCall Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "EndingCallCallback", "()V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("EndingCall End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("EndingCall FAILED");
    return;
}

void ComingCallEnd() {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("ComingCallEnd Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "ComingCallEndCallback", "()V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("ComingCallEnd End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("ComingCallEnd FAILED");
    return;
}

void ComingCallCanceled() {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("ComingCallCanceled Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "ComingCallCanceledCallback", "()V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("ComingCallCanceled End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("ComingCallCanceled FAILED");
    return;
}

void ComingCall(const char *name) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("ComingCall Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "ComingCallCallback",
                                    "(Ljava/lang/String;)V");
    LOGE("ComingCall center");

    if (mid == NULL)
        goto FAIL1;

    js = (*env)->NewStringUTF(env, name);
    (*env)->CallVoidMethod(env, applicationJniObj, mid, js);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("ComingCall End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("ComingCall FAILED");
    return;
}

void getPerCount(VidyoUint participantCount) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("getPerCount Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "meetingPersonCountGet", "(I)V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid, participantCount);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("getPerCount End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("getPerCount FAILED");
    return;
}
void showPerCount(VidyoUint participantCount) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("showPerCount Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "meetingPersonCountShow", "(I)V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid, participantCount);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("showPerCount End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("showPerCount FAILED");
    return;
}

void JoinCallBack() {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("JoinCallBack Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "JoinCallBack", "()V");
//    mid = getApplicationJniMethodId(env, applicationJniObj, "LogOffMustCallback", "(Ljava/lang/String;)V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("JoinCallBack End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("JoinCallBack FAILED");
    return;
}

void LogOffMust(VidyoUint error, VidyoClientSignedOutCause cause) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("LogOffMust Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "LogOffMustCallback", "(I)V");
    LOGE("LogOffMust cause %d",cause);
//    mid = getApplicationJniMethodId(env, applicationJniObj, "LogOffMustCallback", "(Ljava/lang/String;)V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid, error);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("LogOffMust End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("SampleStartConference FAILED");
    return;
}
void mutedAudioIn(VidyoBool jb) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;

    LOGE("mutedAudioIn Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "mutedAudioInCallback", "(Z)V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid, jb);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("mutedAudioIn End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("mutedAudioIn FAILED");
    return;
}
void mutedAudioOut(VidyoBool jb) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    LOGE("mutedAudioOut Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "mutedAudioOutCallback", "(Z)V");

    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid, jb);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("mutedAudioOut End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("mutedAudioOut FAILED");
    return;
}
void mutedAudioInByServer(VidyoBool jb) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    LOGE("mutedAudioInByServer Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "mutedAudioInByServerCallback", "(Z)V");

    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid, jb);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("mutedAudioInByServer End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("mutedAudioInByServer FAILED");
    return;
}
void mutedVideoByServer(VidyoBool jb) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    LOGE("mutedVideoByServer Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "mutedVideoByServerCallback", "(Z)V");

    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid, jb);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("mutedVideoByServer End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("mutedAudioOutByServer FAILED");
    return;
}


void joinRoomWrongCode(VidyoClientError error) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    LOGE("joinRoomWrongCode Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "joinRoomWrongCodeCallback", "(I)V");

    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid, error);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("joinRoomWrongCode End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("joinRoomWrongCode FAILED");
    return;
}

void directCallWrongCode(VidyoClientError error) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    LOGE("directCallWrongCode Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "directCallWrongCodeCallback", "(I)V");

    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid, error);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("directCallWrongCode End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("directCallWrongCode FAILED");
    return;
}

void SampleGuiOnOutEvent(VidyoClientOutEvent event,
                         VidyoVoidPtr param,
                         VidyoUint paramSize,
                         VidyoVoidPtr data) {
    LOGI("GuiOnOutEvent enter Event = %d\n", (int) event);
    if (event == VIDYO_CLIENT_OUT_EVENT_LICENSE) {
        VidyoClientOutEventLicense *eventLicense;
        eventLicense = (VidyoClientOutEventLicense *) param;

        VidyoUint error = eventLicense->error;
        VidyoUint vmConnectionPath = eventLicense->vmConnectionPath;
        VidyoBool OutOfLicenses = eventLicense->OutOfLicenses;

        LOGI("License Error: errorid=%d vmConnectionPath=%d OutOfLicense=%d\n", error,
             vmConnectionPath, OutOfLicenses);
    } else if(event == VIDYO_CLIENT_OUT_EVENT_ROOM_LINK) {
        VidyoClientOutEventRoomLink *eventRoomLink;
        eventRoomLink = (VidyoClientOutEventRoomLink *) param;

        VidyoClientError error = eventRoomLink->error;

        LOGI("VIDYO_CLIENT_OUT_EVENT_ROOM_LINK %d", error);
        joinRoomWrongCode(error);

    } else if (event == VIDYO_CLIENT_OUT_EVENT_SIGN_IN) {
        VidyoClientOutEventSignIn *eventSignIn;
        eventSignIn = (VidyoClientOutEventSignIn *) param;

        VidyoUint activeEid = eventSignIn->activeEid;
        VidyoBool signinSecured = eventSignIn->signinSecured;

        LOGI("activeEid=%d signinSecured=%d\n", activeEid, signinSecured);

        /*
         * If the EID is not setup, it will return activeEid = 0
         * in this case, we invoke the license request using below event
         */
        if (!activeEid)
            (void) VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_LICENSE, NULL, 0);
        VidyoClientRequestCurrentUser user_id;
        VidyoUint ret = VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_GET_CURRENT_USER, &user_id,
                                               sizeof(user_id));
        LOGE("SG: logged in with %d. user_id.CurrentUserID: %s, user_id.CurrentUserDisplay: %s .",
             ret, user_id.currentUserID, user_id.currentUserDisplay);
        LOGE("userId = %s+++%s", user_id.currentUserID, user_id.entityId);
    } else if (event == VIDYO_CLIENT_OUT_EVENT_SIGNED_IN) {
        // Send message to Client/application
        SampleLoginSuccessful();
    } else if (event == VIDYO_CLIENT_OUT_EVENT_SIGNED_OUT) {
        // logout  must
        LOGE("LogOffMust CCCCC");
        VidyoClientOutEventSignedOut *eventLicense;
        eventLicense = (VidyoClientOutEventSignedOut *) param;
        VidyoUint error = eventLicense->error;

        VidyoClientSignedOutCause cause = eventLicense->cause;

        LogOffMust(error, cause);
    } else if (event == VIDYO_CLIENT_OUT_EVENT_REMOTE_SOURCE_ADDED) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_REMOTE_SOURCE_ADDED");
        VidyoClientOutEventRemoteSourceChanged *remoteSource = (VidyoClientOutEventRemoteSourceChanged *) param;
        VidyoClientInEventSetParticipantVideoMode mode = {0};
        mode.actionType = VIDYO_CLIENT_ACTION_TYPE_PIN;
        memcpy(mode.uri, remoteSource->participantURI, sizeof(remoteSource->participantURI));
        mode.videoMode = VIDYO_CLIENT_VIDEO_MODE_PINLOW;
        //NSLog(@"mode.actionType:%d",mode.actionType);
        //NSLog(@"mode.uri:%s",mode.uri);
        //NSLog(@" mode.videoMode:%d", mode.videoMode);
        VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_SET_PARTICIPANT_VIDEO_MODE, &mode,
                             sizeof(VidyoClientInEventSetParticipantVideoMode));

    } else if (event == VIDYO_CLIENT_OUT_EVENT_CONFERENCE_ACTIVE) {
        LOGI("Join Conference Event - received VIDYO_CLIENT_OUT_EVENT_CONFERENCE_ACTIVE\n");
        SampleStartConference();
        joinStatus = 1;
        doResize(x, y);
    } else if (event == VIDYO_CLIENT_OUT_EVENT_CONFERENCE_ENDED) {
        LOGI("Left Conference Event\n");
        SampleEndConference();
        joinStatus = 0;
    } else if (event == VIDYO_CLIENT_OUT_EVENT_PARTICIPANTS_CHANGED) {
        VidyoClientOutEventParticipantsChanged *eventLicense;
        eventLicense = (VidyoClientOutEventParticipantsChanged *) param;

        VidyoUint participantCount = eventLicense->participantCount;
        LOGE("VIDYO_CLIENT_OUT_EVENT_PARTICIPANTS_CHANGED  =  %d", participantCount);
        getPerCount(participantCount);
//		JNIEnv *env;
//		jmethodID mid = getMethodId(env,"meetingPersonCountGet","(I)V");
//		int result = (*env)->CallIntMethod(env, applicationJniObj, mid,participantCount);
    } else if (event == VIDYO_CLIENT_OUT_EVENT_LOGIC_STARTED) {
        LOGE("text  code change start \n");
//        VidyoClientInEventSetFontFile fontEvent;
//        const char *fontFileC = "/data/data/com.telecomyt.videoclient/files/System.vyf";
//        strncpy(fontEvent.fontFileName, fontFileC, sizeof(fontEvent.fontFileName));
//        (void) VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_SET_FONT_FILE, &fontEvent, sizeof(fontEvent));
        LOGE("text  code change end \n");
    } else if (event == VIDYO_CLIENT_OUT_EVENT_FECC_LIST_UPDATED) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_FECC_LIST_UPDATED start \n");
        VidyoClientOutEventFeccListUpdated *eventLicense;
        eventLicense = (VidyoClientOutEventFeccListUpdated *) param;
        VidyoUint numberParticipants = eventLicense->numberParticipants;
        showPerCount(numberParticipants);
        LOGE("VIDYO_CLIENT_OUT_EVENT_FECC_LIST_UPDATED end \n");
    }
        /*else if(event == VIDYO_CLIENT_OUT_EVENT_ADD_SHARE)
        {
            VidyoClientRequestWindowShares shareRequest;
            VidyoUint Result;

            LOGI("VIDYO_CLIENT_OUT_EVENT_ADD_SHARE\n");
            memset(&shareRequest, 0, sizeof(shareRequest));
            shareRequest.requestType = LIST_SHARING_WINDOWS;
             VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_GET_WINDOW_SHARES,
                                                  &shareRequest,
                                                  sizeof(shareRequest));
            if (Result != VIDYO_CLIENT_ERROR_OK)
            {
                LOGE("VIDYO_CLIENT_REQUEST_GET_WINDOW_SHARES failed");
            }
            else
            {
                LOGI("VIDYO_CLIENT_REQUEST_GET_WINDOW_SHARES success:%d, %d", shareRequest.shareList.numApp, shareRequest.shareList.currApp);

                shareRequest.shareList.newApp = shareRequest.shareList.currApp = 1;
                shareRequest.requestType = ADD_SHARING_WINDOW;

                Result = VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_SET_WINDOW_SHARES,
                                                  &shareRequest,
                                                  sizeof(shareRequest));

                if (Result != VIDYO_CLIENT_ERROR_OK)
                {
                    LOGE("VIDYO_CLIENT_REQUEST_SET_WINDOW_SHARES failed\n");

                }
                else
                {
                    LOGI("VIDYO_CLIENT_REQUEST_SET_WINDOW_SHARES success\n");
                }
            }
        }*/
    else if (event == VIDYO_CLIENT_OUT_EVENT_DEVICE_SELECTION_CHANGED) {
        VidyoClientOutEventDeviceSelectionChanged *eventOutDeviceSelectionChg = (VidyoClientOutEventDeviceSelectionChanged *) param;

        if (eventOutDeviceSelectionChg->changeType ==
            VIDYO_CLIENT_USER_MESSAGE_DEVICE_SELECTION_CHANGED) {
            if (eventOutDeviceSelectionChg->deviceType == VIDYO_CLIENT_DEVICE_TYPE_VIDEO) {
                SampleSwitchCamera((char *) eventOutDeviceSelectionChg->newDeviceName);
            }
        }
    } else if (event == VIDYO_CLIENT_OUT_EVENT_MUTED_VIDEO){
        SampleSwitchCamera("");
    } else if (event == VIDYO_CLIENT_OUT_EVENT_MUTED_AUDIO_IN){
        VidyoClientOutEventMuted *eventIn = (VidyoClientOutEventMuted *) param;
        VidyoBool bIn = eventIn->isMuted;
        mutedAudioIn(bIn);
    } else if (event == VIDYO_CLIENT_OUT_EVENT_MUTED_AUDIO_OUT){
        VidyoClientOutEventMuted *eventOut = (VidyoClientOutEventMuted *) param;
        VidyoBool bOut = eventOut->isMuted;
        mutedAudioOut(bOut);
    }else if (event == VIDYO_CLIENT_OUT_EVENT_MUTED_SERVER_AUDIO_IN){
        VidyoClientOutEventMuted *eventSIn = (VidyoClientOutEventMuted *) param;
        VidyoBool bsOut = eventSIn->isMuted;
        mutedAudioInByServer(bsOut);
    }else if (event == VIDYO_CLIENT_OUT_EVENT_MUTED_SERVER_VIDEO){
        VidyoClientOutEventMuted *bsVideo = (VidyoClientOutEventMuted *) param;
        VidyoBool bsVideo1 = bsVideo->isMuted;
        mutedVideoByServer(bsVideo1);
    }else if (event == VIDYO_CLIENT_OUT_EVENT_LOGIC_STARTED) {
        LOGI("Library Started Event\n");
        LibraryStarted();
    } else if (event == VIDYO_CLIENT_OUT_EVENT_CONFERENCE_ACTIVE_AFTER_RETRY) {
        LOGE("join Conference RETRY\n");
    } else if (event == VIDYO_CLIENT_OUT_EVENT_SUBSCRIBING) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_SUBSCRIBING \n");
    } else if (event == VIDYO_CLIENT_OUT_EVENT_APP_EXIT) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_APP_EXIT \n");
    } else if (event == VIDYO_CLIENT_OUT_EVENT_JOINING) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_JOINING \n");
        JoinCallBack();
    } else if (event == VIDYO_CLIENT_OUT_EVENT_CALLING) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_CALLING \n");
    } else if (event == VIDYO_CLIENT_OUT_EVENT_CALL_PROGRESS) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_CALL_PROGRESS \n");
    }
        //B用户收到点对点会议请求 的对应api
    else if (event == VIDYO_CLIENT_OUT_EVENT_INCOMING_CALL) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_INCOMING_CALL \n");

        VidyoClientOutEventIncomingCall *eventLicense;
        eventLicense = (VidyoClientOutEventIncomingCall *) param;

        ComingCall((char *) eventLicense->invitingUser);
    }
        //被呼叫者没接  呼叫者挂掉了
    else if (event == VIDYO_CLIENT_OUT_EVENT_END_INCOMING_CALL) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_END_INCOMING_CALL \n");
        ComingCallCanceled();
    }
        //A用户收到B用户拒绝或接受 的api :messageType=VIDYO_CLIENT_USER_MESSAGE_TYPE_CALLEE_DECLINED
        //B用户收到A用户取消呼叫:"messageType=VIDYO_CLIENT_USER_MESSAGE_TYPE_CALLER_CANCELLED
    else if (event == VIDYO_CLIENT_OUT_EVENT_USER_MESSAGE) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_USER_MESSAGE \n");
        VidyoClientOutEventUserMessage *msg;
        msg = (VidyoClientOutEventUserMessage *) param;

        if (msg->messageType == VIDYO_CLIENT_USER_MESSAGE_TYPE_CALLEE_DECLINED) {
            ComingCallEnd();
        }
    }
        //呼叫结束？自己呼叫自己的时候 会直接调用
    else if (event == VIDYO_CLIENT_OUT_EVENT_END_CALLING) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_END_CALLING \n");
        EndingCall();
    }else if (event == VIDYO_CLIENT_OUT_EVENT_GROUP_CHAT) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_GROUP_CHAT \n");
        VidyoClientOutEventGroupChat *msg;
        msg = (VidyoClientOutEventGroupChat *) param;
        ComingMsgG((char *) msg->uri,(char *) msg->displayName,(char *) msg->	message);
    }else if (event == VIDYO_CLIENT_OUT_EVENT_PORTAL_SERVICE) {
        LOGE("VIDYO_CLIENT_OUT_EVENT_PORTAL_SERVICE \n");
//        VidyoClientOutEventPortalService *result;
//        result = (VidyoClientOutEventPortalService *) param;
//        VidyoClientError error = result.response.directCall->error;
//
//        directCallWrongCode(error);
    }
}

void LibraryStarted() {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("LibraryStarted Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "libraryStartedCallback", "()V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("LibraryStarted End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("LibraryStarted FAILED");
    return;
}

void SampleStartConference() {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("SampleStartConference Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "callStartedCallback", "()V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("SampleStartConference End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("SampleStartConference FAILED");
    return;
}

void SampleLoginSuccessful() {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("SampleLoginSuccessful Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "loginSuccessfulCallback", "()V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("SampleLoginSuccessful End");
    return;
    FAIL1:
    if (isAttached) {
        LOGE("SampleLoginSuccessful FAILED  isAttached");
        mid = getApplicationJniMethodId(env, applicationJniObj, "loginFailCallback", "()V");
        (*env)->CallVoidMethod(env, applicationJniObj, mid);
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:

    mid = getApplicationJniMethodId(env, applicationJniObj, "loginFailCallback", "()V");
    (*env)->CallVoidMethod(env, applicationJniObj, mid);
    LOGE("SampleLoginSuccessful FAILED");
    return;

}

//char getDefFileDirs() {
//
//    jboolean isAttached;
//    JNIEnv *env;
//    jmethodID mid;
//    jstring js;
//
//    LOGE("getDefFileDirs Begin");
//
//    env = getJniEnv(&isAttached);
//
//    mid = getApplicationJniMethodId(env, applicationJniObj, "getDefFileDirs", "()Ljava/lang/String;");
//
//    if (isAttached) {
//        (*global_vm)->DetachCurrentThread(global_vm);
//    }
//
//     js =(jstring)env->CallObjectMethod(env, applicationJniObj, mid);
////    const char *portalC = (*env)->GetStringUTFChars(env, js, NULL);
//
//    LOGE("getDefFileDirs End");
//
//    return js;
//}

void SampleEndConference() {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("SampleEndConference Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "callEndedCallback", "()V");
    if (mid == NULL)
        goto FAIL1;

    (*env)->CallVoidMethod(env, applicationJniObj, mid);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("SampleEndConference End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("SampleEndConference FAILED");
    return;
}


void SampleSwitchCamera(const char *name) {
    jboolean isAttached;
    JNIEnv *env;
    jmethodID mid;
    jstring js;
    LOGE("SampleSwitchCamera Begin");
    env = getJniEnv(&isAttached);
    if (env == NULL)
        goto FAIL0;

    mid = getApplicationJniMethodId(env, applicationJniObj, "cameraSwitchCallback", "(Ljava/lang/String;)V");
    if (mid == NULL)
        goto FAIL1;

    js = (*env)->NewStringUTF(env, name);
    (*env)->CallVoidMethod(env, applicationJniObj, mid, js);

    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    LOGE("SampleSwitchCamera End");
    return;
    FAIL1:
    if (isAttached) {
        (*global_vm)->DetachCurrentThread(global_vm);
    }
    FAIL0:
    LOGE("SampleSwitchCamera FAILED");
    return;
}

static jobject *SampleInitCacheClassReference(JNIEnv *env, const char *classPath) {
    jclass appClass = (*env)->FindClass(env, classPath);
    if (!appClass) {
        LOGE("cacheClassReference: Failed to find class %s", classPath);
        return ((jobject *) 0);
    }

    jmethodID mid = (*env)->GetMethodID(env, appClass, "<init>", "()V");
    if (!mid) {
        LOGE("cacheClassReference: Failed to construct %s", classPath);
        return ((jobject *) 0);
    }
    jobject obj = (*env)->NewObject(env, appClass, mid);
    if (!obj) {
        LOGE("cacheClassReference: Failed to create object %s", classPath);
        return ((jobject *) 0);
    }
    return (*env)->NewGlobalRef(env, obj);
}

JNIEXPORT void
Java_com_telecomyt_videolibrary_VideoClient_Construct(JNIEnv *env, jobject javaThis,
                                                             jstring caFilename, jstring logDir,
                                                             jstring pathDir,
                                                             jobject defaultActivity) {

    FUNCTION_ENTRY;


    VidyoClientAndroidRegisterDefaultVM(global_vm);
    VidyoClientAndroidRegisterDefaultApp(env, defaultActivity);

    const char *pathDirC = (*env)->GetStringUTFChars(env, pathDir, NULL);
    const char *logDirC = (*env)->GetStringUTFChars(env, logDir, NULL);
    const char *certificatesFileNameC = (*env)->GetStringUTFChars(env, caFilename, NULL);


    //const char *logBaseFileName = "VidyoClientSample_";
    //const char *installedDirPath = NULL;
    //static const VidyoUint DEFAULT_LOG_SIZE = 1000000;
    //const char *logLevelsAndCategories = "fatal error warning debug@App info@AppEmcpClient debug@LmiApp debug@AppGui info@AppGui";
    VidyoRect videoRect = {(VidyoInt)(0), (VidyoInt)(0), (VidyoUint)(100), (VidyoUint)(100)};
    //VidyoUint logSize = DEFAULT_LOG_SIZE;

    applicationJniObj = SampleInitCacheClassReference(env, "com/telecomyt/videolibrary/VideoClientOutEvent");
    // This will start logging to LogCatcom.lunry.jwmeeting
    // Use mainly for debugging purposes
    VidyoClientConsoleLogConfigure(VIDYO_CLIENT_CONSOLE_LOG_CONFIGURATION_ALL);

    // Start the VidyoClient Library

    /* VidyoBool returnValue = VidyoClientStart(SampleGuiOnOutEvent,
     NULL,
     "/data/data/com.vidyo.vidyosample/cache/",
     logBaseFileName,
     "/data/data/com.vidyo.vidyosample/files/",
     logLevelsAndCategories,
     logSize,
     (VidyoWindowId)(0),
     &videoRect,
     NULL,
     &profileParam,NULL);
     if (returnValue)
*/
    VidyoClientLogParams logParam = {0};
    logParam.logLevelsAndCategories = "fatal error warning debug@App info@AppEmcpClient debug@LmiApp debug@AppGui info@AppGui";
    logParam.logSize = 5000000;
//    logParam.pathToLogDir = "/data/data/com.vidyo.vidyosample/cache/";
    logParam.pathToLogDir = logDirC;
    logParam.logBaseFileName = "VidyoClientSample_";
//    logParam.pathToDumpDir = "/data/data/com.vidyo.vidyosample/files/";
    logParam.pathToDumpDir = logDirC;
    logParam.pathToConfigDir = pathDirC;


    LOGE("ApplicationJni_Construct: certifcateFileName=%s, configDir=%s, logDir=%s!\n",
         certificatesFileNameC, pathDirC, logDirC);


    //modify and test by huan start
//    VidyoClientFeatureControl featureControlParam = {0};
//    VidyoClientFeatureControlConstructDefault(&featureControlParam);
//    featureControlParam.androidForceResolution = VIDYO_TRUE;
//    VidyoBool returnValue = VidyoClientSetOptionalFeatures(&featureControlParam);
//    if (returnValue) {
//        LOGI("VidyoClientSetOptionalFeatures() was a SUCCESS\n");
//    } else {
//        //start failed
//        LOGE("VidyoClientSetOptionalFeatures() returned error!\n");
//    }
    //modify and test by huan end


    VidyoBool returnValue = VidyoClientStart(SampleGuiOnOutEvent,
                                             NULL,
                                             &logParam,
                                             (VidyoWindowId)(0),
                                             &videoRect,
                                             NULL,
                                             NULL,
                                             NULL);
    if (returnValue) {
        LOGI("VidyoClientStart() was a SUCCESS\n");
    } else {
        //start failed
        LOGE("ApplicationJni_Construct VidyoClientStart() returned error!\n");
    }

    AppCertificateStoreInitialize(logDirC, certificatesFileNameC, NULL);

    FUNCTION_EXIT;
}

static void sig_handler(int signo) {
    LOGE("sig_handler");
    if (signo == SIGKILL) {
        LOGE("sig_handler KILL");
    }
}

void catch_child_dead_signal() {
    LOGE("catch_child_dead_signal");

    struct sigaction sa;

    sigemptyset(&sa.sa_mask);

    sa.sa_flags = 0;

    sa.sa_handler = sig_handler;
    int i = 0;
    for (i; i < 32; i++) {
        sigaction(i, &sa, NULL);
    }
//sigaction( SIGTERM, &sa, NULL );

}

JNIEXPORT void
Java_com_telecomyt_videolibrary_VideoClient_Login(JNIEnv *env, jobject javaThis,
                                                         jstring vidyoportalName, jstring userName,
                                                         jstring passwordName) {

    catch_child_dead_signal();

    FUNCTION_ENTRY;
    LOGI("Java_com_telecomyt_videolibrary_VideoClient_Login() enter\n");

    const char *portalC = (*env)->GetStringUTFChars(env, vidyoportalName, NULL);
    const char *usernameC = (*env)->GetStringUTFChars(env, userName, NULL);
    const char *passwordC = (*env)->GetStringUTFChars(env, passwordName, NULL);

    LOGI("Starting Login Process\n");
    VidyoClientInEventLogIn event = {0};

    strlcpy(event.portalUri, portalC, sizeof(event.portalUri));
    strlcpy(event.userName, usernameC, sizeof(event.userName));
    strlcpy(event.userPass, passwordC, sizeof(event.userPass));

    LOGI("logging in with portalUri %s user %s ", event.portalUri, event.userName);
    VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_LOGIN, &event, sizeof(VidyoClientInEventLogIn));
    FUNCTION_EXIT;
}

JNIEXPORT void
Java_com_telecomyt_videolibrary_VideoClient_SignOff(JNIEnv *env, jobject javaThis) {

    FUNCTION_ENTRY;
    LOGE("Java_com_telecomyt_videolibrary_VideoClient_Logout() enter\n");
    VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_SIGNOFF, NULL, 0);
    LOGE("Java_com_telecomyt_videolibrary_VideoClient_Logout() out\n");
    FUNCTION_EXIT;
}


JNIEXPORT void JNICALL Java_com_telecomyt_videolibrary_VideoClient_Dispose(JNIEnv *env,jobject jObj2)
{
FUNCTION_ENTRY;
if (VidyoClientStop())
LOGI("VidyoClientStop() SUCCESS!!\n");
else
LOGE("VidyoClientStop() FAILURE!!\n");
FUNCTION_EXIT;
}

JNIEXPORT jint JNICALL JNI_OnLoad
        (JavaVM *vm, void *pvt)
{
    FUNCTION_ENTRY;
    LOGI("JNI_OnLoad called\n");
    global_vm = vm;
    FUNCTION_EXIT;
    return JNI_VERSION_1_4;
}

JNIEXPORT void JNICALL JNI_OnUnload( JavaVM *vm,void *pvt)
{
FUNCTION_ENTRY;
//	FUNCTION_ENTRY
LOGE("JNI_OnUnload called\n");
FUNCTION_EXIT;
//	FUNCTION_EXIT
}

JNIEXPORT void JNICALL Java_com_telecomyt_videolibrary_VideoClient_Render
(JNIEnv *env,jobject jObj2)
{
//	FUNCTION_ENTRY;
doRender();
//	FUNCTION_EXIT;
}


JNIEXPORT void JNICALL Java_com_telecomyt_videolibrary_VideoClient_RenderRelease
(JNIEnv *env,jobject jObj2)
{
FUNCTION_ENTRY;

doSceneReset();

FUNCTION_EXIT;
}

void JNICALL Java_com_telecomyt_videolibrary_VideoClient_Resize
(JNIEnv *env,jobject jobj, jint width,jint height)
{
FUNCTION_ENTRY;
LOGI("JNI Resize width=%d height=%d\n", width, height);
x = width;
y = height;
doResize((VidyoUint)
width, (VidyoUint)height);
FUNCTION_EXIT;
}


JNIEXPORT void JNICALL Java_com_telecomyt_videolibrary_VideoClient_TouchEvent
(JNIEnv *env,jobject jobj, jint id,jint type, jint x,jint y)
{
FUNCTION_ENTRY;
doTouchEvent((VidyoInt)
id, (VidyoInt)type, (VidyoInt)x, (VidyoInt)y);
FUNCTION_EXIT;
}


JNIEXPORT void JNICALL Java_com_telecomyt_videolibrary_VideoClient_SetOrientation
(JNIEnv*env,jobject jobj, jint orientation)
{
FUNCTION_ENTRY;

VidyoClientOrientation newOrientation = VIDYO_CLIENT_ORIENTATION_UP;

//translate LMI orienation to client orientation
switch(orientation) {
case 0:
newOrientation = VIDYO_CLIENT_ORIENTATION_UP;
LOGI("VIDYO_CLIENT_ORIENTATION_UP");
break;
case 1:
newOrientation = VIDYO_CLIENT_ORIENTATION_DOWN;
LOGI("VIDYO_CLIENT_ORIENTATION_DOWN");
break;
case 2:
newOrientation = VIDYO_CLIENT_ORIENTATION_LEFT;
LOGI("VIDYO_CLIENT_ORIENTATION_LEFT");
break;
case 3:
newOrientation = VIDYO_CLIENT_ORIENTATION_RIGHT;
LOGI("VIDYO_CLIENT_ORIENTATION_RIGHT");
break;
}

doClientSetOrientation(newOrientation);

FUNCTION_EXIT;
return;
}

JNIEXPORT void JNICALL Java_com_telecomyt_videolibrary_VideoClient_SetCameraDevice(JNIEnv *env,jobject jobj, jint camera,jint selfView)
{
// FUNCTION_ENTRY
VidyoClientRequestConfiguration requestConfig;
VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_GET_CONFIGURATION,&requestConfig, sizeof(VidyoClientRequestConfiguration));

/*
 * Value of 0 is (currently) used to signify the front camera
 */
//if (camera == 0)
//{
//requestConfig.currentCamera = 0;
//}
/*
 * Value of 1 is (currently) used to signify the back camera
 */
//else if (camera == 1)
//{
//requestConfig.currentCamera = 1;
//}
requestConfig.currentCamera = camera;
requestConfig.selfViewLoopbackPolicy = selfView;
VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_SET_CONFIGURATION,&requestConfig, sizeof(VidyoClientRequestConfiguration));
//FUNCTION_EXIT
}


JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_DriveUsed(JNIEnv
*env,
jobject jobj,
        jint
camera,
jint speaker, jint
microphone)
{
VidyoClientRequestConfiguration requestConfig;
VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_GET_CONFIGURATION,
&requestConfig, sizeof(VidyoClientRequestConfiguration));
if(camera!=-1){
requestConfig.
enableHideCameraOnJoin = camera;
}
if(speaker!=-1){
requestConfig.
enableMuteSpeakerOnJoin = speaker;
}
if(microphone!=-1){
requestConfig.
enableMuteMicrophoneOnJoin = microphone;
}
VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_SET_CONFIGURATION,
&requestConfig, sizeof(VidyoClientRequestConfiguration));
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_DisableAutoLogin(JNIEnv
*env,
jobject jobj
)
{
//FUNCTION_ENTRY
VidyoClientRequestConfiguration requestConfig;
VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_GET_CONFIGURATION,
&requestConfig, sizeof(VidyoClientRequestConfiguration));
requestConfig.
enableAutoLogIn = 0;
VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_SET_CONFIGURATION,
&requestConfig, sizeof(VidyoClientRequestConfiguration));
//FUNCTION_EXIT
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_SetPreviewModeON(JNIEnv
*env,
jobject jobj, jboolean
pip)
{
VidyoClientInEventPreview event;
if (pip)
event.
previewMode = VIDYO_CLIENT_PREVIEW_MODE_DOCK;
else
event.
previewMode = VIDYO_CLIENT_PREVIEW_MODE_NONE;
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_PREVIEW,
&event, sizeof(VidyoClientInEventPreview));
}

void _init() {
    FUNCTION_ENTRY;
    LOGE("_init called\n");
    FUNCTION_EXIT;
}

void _fini() {
    FUNCTION_ENTRY;
    LOGE("_fini called\n");
    FUNCTION_EXIT;
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_DisableAllVideoStreams(JNIEnv
*env,
jobject jobj
)
{
if (!allVideoDisabled)
{
//this would have the effect of stopping all video streams but self preview

VidyoClientRequestSetBackground reqBackground = {0};
reqBackground.
willBackground = VIDYO_TRUE;
(void)
VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_SET_BACKGROUND,
&reqBackground, sizeof(reqBackground));

allVideoDisabled = VIDYO_TRUE;
}
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_EnableAllVideoStreams(JNIEnv
*env,
jobject jobj
)
{
{
if (allVideoDisabled)
{
VidyoClientRequestSetBackground reqBackground = {0};
reqBackground.
willBackground = VIDYO_FALSE;
(void)
VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_SET_BACKGROUND,
&reqBackground, sizeof(reqBackground));

//this would have the effect of enabling all video streams
allVideoDisabled = VIDYO_FALSE;
//			rearrangeSceneLayout();
}
}
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_MuteCamera(JNIEnv
*env,
jobject jobj, jboolean
MuteCamera)
{
VidyoClientInEventMute event;
event.
willMute = MuteCamera;
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_MUTE_VIDEO,
&event, sizeof(VidyoClientInEventMute));
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_StartConferenceMedia(JNIEnv
*env,
jobject jobj
)
{
doStartConferenceMedia();

}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_HideToolBar(JNIEnv
* env,
jobject jobj, jboolean
disablebar)
{//before conference start  when init used
LOGI("Java_com_telecomyt_videolibrary_VideoClient_HideToolBar() enter\n");
VidyoClientInEventEnable event;
//event.willEnable = VIDYO_TRUE;
event.
willEnable = VIDYO_FALSE;
VidyoBool ret = VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_ENABLE_BUTTON_BAR, &event,
                                     sizeof(VidyoClientInEventEnable));
if (!ret)
LOGW("Java_com_telecomyt_videolibrary_VideoClient_HideToolBar() failed!\n");
}
JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_Answer(JNIEnv
* env,
jobject jobj
)
{//before conference start  when init used
LOGE("Java_com_telecomyt_videolibrary_VideoClient_Answer() enter\n");
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_ANSWER, NULL,
0);
LOGE("Java_com_telecomyt_videolibrary_VideoClient_Answer() out\n");
}
JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_Decline(JNIEnv
* env,
jobject jobj
)
{//before conference start  when init used
LOGE("Java_com_telecomyt_videolibrary_VideoClient_Decline() enter\n");
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_DECLINE, NULL,
0);
LOGE("Java_com_telecomyt_videolibrary_VideoClient_Decline() out\n");
}

// this function will enable echo cancellation
JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_SetEchoCancellation(JNIEnv
*env,
jobject jobj, jboolean
aecenable)
{
// get persistent configuration values
VidyoClientRequestConfiguration requestConfiguration;

VidyoUint ret = VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_GET_CONFIGURATION,
                                       &requestConfiguration,
        //sizeof(VidyoClientRequestConfiguration));
                                       sizeof(requestConfiguration));
if (ret != VIDYO_CLIENT_ERROR_OK) {
LOGE("VIDYO_CLIENT_REQUEST_GET_CONFIGURATION returned error!");
return;
}
// modify persistent configuration values, based on current values of on-screen controls
if (aecenable) {
requestConfiguration.
enableEchoCancellation = 1;
} else {
requestConfiguration.
enableEchoCancellation = 0;
}
//requestConfiguration.currentSpeaker = 0;
//requestConfiguration.currentVidyoProxy = 1;
//int i=0;
//for(i=0;i<strlen(requestConfiguration.speakers);i++){
//LOGE("xys = %d %c",i,requestConfiguration.speakers[i]);
//LOGE("xys = speaker %d %s",i,requestConfiguration.speakers[i]);
//}

//LOGE("requestConfiguration.speakers length = %d",strlen(requestConfiguration.speakers));
//LOGE("requestConfiguration.numberSpeakers length = %d",requestConfiguration.numberSpeakers);
//int len = (*env)->GetArrayLength(env, requestConfiguration.speakers);
//LOGE("requestConfiguration.speakers size = %d",len);
// set persistent configuration values
ret = VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_SET_CONFIGURATION, &requestConfiguration,
        //sizeof(VidyoClientRequestConfiguration));
                             sizeof(requestConfiguration));
if (ret != VIDYO_CLIENT_ERROR_OK) {
LOGE("VIDYO_CLIENT_REQUEST_SET_CONFIGURATION returned error!");
}
}
JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_SetSpeakerVolume(JNIEnv
*env,
jobject jobj, jint
volume)
{
//FUNCTION ENTRY
VidyoClientRequestVolume volumeRequest;
volumeRequest.
volume = volume;
VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_SET_VOLUME_AUDIO_OUT,
&volumeRequest,
sizeof(VidyoClientRequestVolume));
//sizeof(volumeRequest));
//FUNCTION EXIT
return;
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_DisableShareEvents(JNIEnv
*env,
jobject javaThisj
)
{
FUNCTION_ENTRY
VidyoClientSendEvent (VIDYO_CLIENT_IN_EVENT_DISABLE_SHARE_EVENTS,
0, 0);
LOGI("Disable Shares Called - Vimal");
FUNCTION_EXIT;
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_ParticipantsLimit(JNIEnv
*env,
jobject javaThisj, jint
i)
{
FUNCTION_ENTRY;
LOGE("ParticipantsLimit IN");
VidyoClientInEventParticipantsLimit limit;
limit.
maxNumParticipants = i;
VidyoClientSendRequest(VIDYO_CLIENT_IN_EVENT_PARTICIPANTS_LIMIT,
&limit,
sizeof(VidyoClientInEventParticipantsLimit));
LOGE("ParticipantsLimit OUT");
FUNCTION_EXIT;
}


JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_sendConference(
        JNIEnv
*env,
jobject javaThis
)
{
FUNCTION_ENTRY
LOGE("sendConference() enter\n");

char *callName = "38";
VidyoClientInEventPortalService event = {0};
VidyoClientPortalServiceDirectCall call = {0};

call.
typeRequest = VIDYO_CLIENT_SERVICE_TYPE_DIRECT_CALL;
call.
requestId = 13528627;
call.
muteCamera = VIDYO_TRUE;
call.
muteMicrophone = VIDYO_TRUE;
call.
muteSpeaker = VIDYO_TRUE;
call.
entityType = VIDYO_CLIENT_ENTITY_TYPE_MEMBER;
strlcpy(call
.entityID, callName , sizeof(call.entityID));

event.requests.
directCall = call;

//if (VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_PORTAL_SERVICE, &event, sizeof(VidyoClientInEventPortalService)) == true)
//{
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_PORTAL_SERVICE,
&event, sizeof(VidyoClientInEventPortalService));
//}

LOGE("sendConference() exit\n");

FUNCTION_EXIT;
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_joinRoomConference(
        JNIEnv
*env,
jobject javaThis, jstring
protal,
jstring roomKey, jstring
name,
jboolean muteCamera, jboolean
muteMicrophone,
jboolean muteSpeaker,
jstring pin
)
{
FUNCTION_ENTRY;
LOGE("joinRoomConference() enter\n");

VidyoClientInEventRoomLink event0 = {0};

const char *roomKeyC = (*env)->GetStringUTFChars(env, roomKey, NULL);
const char *nameC = (*env)->GetStringUTFChars(env, name, NULL);
const char *protalC = (*env)->GetStringUTFChars(env, protal, NULL);
const char *pinC = (*env)->GetStringUTFChars(env, pin, NULL);

strlcpy(event0
.portalUri, protalC, sizeof(event0.portalUri));
strlcpy(event0
.roomKey, roomKeyC, sizeof(event0.roomKey));
strlcpy(event0
.displayName, nameC, sizeof(event0.displayName));
strlcpy(event0
.pin, pinC, sizeof(event0.pin));

event0.
muteCamera = muteCamera;
event0.
muteMicrophone = muteMicrophone;
event0.
muteSpeaker = muteSpeaker;

VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_ROOM_LINK,
&event0, sizeof(VidyoClientInEventRoomLink));

LOGE("joinRoomConference() exit\n");
FUNCTION_EXIT;
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_joinRoomConferenceEX(
        JNIEnv
*env,
jobject javaThis, jstring
protal,
jstring roomKey, jstring
name)
{
FUNCTION_ENTRY
LOGE("joinRoomConference() enter\n");

VidyoClientInEventRoomLinkEx event0 = {0};

const char *roomKeyC = (*env)->GetStringUTFChars(env, roomKey, NULL);
const char *nameC = (*env)->GetStringUTFChars(env, name, NULL);
const char *protalC = (*env)->GetStringUTFChars(env, protal, NULL);

//strlcpy(event0.portalUri, "172.18.191.206", sizeof(event0.portalUri));
strlcpy(event0
.portalUri, protalC, sizeof(event0.portalUri));
strlcpy(event0
.roomKey, roomKeyC, sizeof(event0.roomKey));
strlcpy(event0
.displayName, nameC, sizeof(event0.displayName));

// if (VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_ROOM_LINK, &event0, sizeof(VidyoClientInEventRoomLink)) == true)
//{
//}
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_ROOM_LINK_EX,
&event0, sizeof(VidyoClientInEventRoomLinkEx));

LOGE("joinRoomConference() exit\n");

FUNCTION_EXIT;
}

JNIEXPORT void Java_com_telecomyt_videolibrary_VideoClient_DirectCall
        (JNIEnv * env, jobject jobj,jstring who) {
const char *member = (*env)->GetStringUTFChars(env, who, NULL);
VidyoClientInEventPortalService service = {0};
VidyoClientPortalServiceDirectCall event = {0};
strlcpy(event.entityID, member, sizeof(event.entityID));
event.entityType = VIDYO_CLIENT_ENTITY_TYPE_MEMBER;
event.typeRequest = 0;
memcpy(&(service.requests.directCall),&event,sizeof(event));
LOGE("Making direct call %s", member);
VidyoBool ret = VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_PORTAL_SERVICE, &event,
                                     sizeof(VidyoClientInEventPortalService));
if (!ret)
LOGE("Java_com_vidyo_vidyosample_VidyoSampleApplication_DirectCall() failed!\n");
}


JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_setAllOrientation(
        JNIEnv
*env,
jobject javaThis
)
{
//VIDYO_CLIENT_ORIENTATION_UP = 0,
//VIDYO_CLIENT_ORIENTATION_DOWN,
//VIDYO_CLIENT_ORIENTATION_LEFT,
//VIDYO_CLIENT_ORIENTATION_RIGHT,

//VIDYO_CLIENT_ORIENTATION_CONTROL_BOTH = 0,  /*!< Self-view and remote participants */
//VIDYO_CLIENT_ORIENTATION_CONTROL_CAPTURE,   /*!< Self-view only */
//VIDYO_CLIENT_ORIENTATION_CONTROL_RENDER,    /*!< Remote participants only */

FUNCTION_ENTRY
LOGE("setAllOrientation() enter\n");

VidyoClientInEventSetOrientation event0 = {0};

event0.
orientation = VIDYO_CLIENT_ORIENTATION_DOWN;
event0.
control = VIDYO_CLIENT_ORIENTATION_CONTROL_BOTH;

LOGE("setAllOrientation() exit\n");

FUNCTION_EXIT;
}
JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_micEnable(
        JNIEnv
*env,
jobject javaThis
)
{
//typedef enum VidyoClientPrecallDeviceTestAction_
//{
///*! Start testing the device */
//VIDYO_CLIENT_DEVICE_TEST_START=1,
///*! Stop testing the device */
//VIDYO_CLIENT_DEVICE_TEST_STOP,
// } VidyoClientPrecallDeviceTestAction;

FUNCTION_ENTRY

LOGE("micCanUse() enter\n");

VidyoClientInEventMute event0 = {0};

event0.
willMute = VIDYO_FALSE;

VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_MUTE_AUDIO_IN,
&event0, sizeof(VidyoClientInEventMute));

LOGE("micCanUse() exit\n");

FUNCTION_EXIT;
}
JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_micDisenable(
        JNIEnv
*env,
jobject javaThis
)
{
FUNCTION_ENTRY

LOGE("micCaNotUse() enter\n");
VidyoClientInEventMute event0 = {0};

event0.
willMute = VIDYO_TRUE;

VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_MUTE_AUDIO_IN,
&event0, sizeof(VidyoClientInEventMute));
LOGE("micCaNotUse() exit\n");

FUNCTION_EXIT;
}
JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_stopConference(
        JNIEnv
*env,
jobject javaThis
)
{
FUNCTION_ENTRY

LOGE("stopConference() enter\n");
VidyoClientInEventSetOffline event0 = {0};

event0.
offline = VIDYO_TRUE;

VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_CANCEL,
&event0, sizeof(VidyoClientInEventSetOffline));
LOGE("stopConference() exit\n");

FUNCTION_EXIT;
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_SimpleConference(
        JNIEnv
*env,
jobject javaThis
)
{
FUNCTION_ENTRY

LOGE("SimpleConference() enter\n");

VidyoClientInEventParticipantsLimit event = {0};
event.
maxNumParticipants = 01;
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_PARTICIPANTS_LIMIT,
&event, sizeof(VidyoClientInEventParticipantsLimit));

LOGE("SimpleConference() exit\n");

FUNCTION_EXIT;
}
JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_listenerEnable(
        JNIEnv
*env,
jobject javaThis
)
{
FUNCTION_ENTRY

LOGE("ListenerCanUse() enter\n");

VidyoClientInEventMute event = {0};
event.
willMute = VIDYO_FALSE;
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_MUTE_AUDIO_OUT,
&event, sizeof(VidyoClientInEventMute));

LOGE("ListenerCanUse() exit\n");

FUNCTION_EXIT;
}
JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_ListenerDisenable(
        JNIEnv
*env,
jobject javaThis
)
{
FUNCTION_ENTRY

LOGE("ListenerCanNotUse() enter\n");

VidyoClientInEventMute event = {0};
event.
willMute = VIDYO_TRUE;
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_MUTE_AUDIO_OUT,
&event, sizeof(VidyoClientInEventMute));

LOGE("ListenerCanNotUse() exit\n");

FUNCTION_EXIT;
}
JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_CameraDisenable(
        JNIEnv
*env,
jobject javaThis
)
{
FUNCTION_ENTRY;

LOGE("CameraDisenable() enter\n");

VidyoClientInEventMute event = {0};
event.
willMute = VIDYO_TRUE;
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_MUTE_VIDEO,
&event, sizeof(VidyoClientInEventMute));

LOGE("CameraDisenable() exit\n");

FUNCTION_EXIT;
}
JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_CameraEnable(
        JNIEnv
*env,
jobject javaThis
)
{
FUNCTION_ENTRY;

LOGE("CameraEnable() enter\n");

VidyoClientInEventMute event = {0};
event.
willMute = VIDYO_FALSE;
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_MUTE_VIDEO,
&event, sizeof(VidyoClientInEventMute));

LOGE("CameraEnable() exit\n");

FUNCTION_EXIT;
}
JNIEXPORT void JNICALL Java_com_telecomyt_videolibrary_VideoClient_changeCamera(JNIEnv *env,jobject javaThis, jint camera)
{
FUNCTION_ENTRY;

LOGE("ListenerCanNotUse() enter\n");

VidyoVideoCapturerLocation capEvent = VIDYO_VIDEOCAPTURERLOCATION_Front;
//requestConfig.currentCamera  = 1;
//requestConfig.currentCamera  = 0;
capEvent = camera;
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_TOGGLE_CAMERA,
&capEvent,sizeof(VidyoVideoCapturerLocation));

LOGE("ListenerCanNotUse() exit\n");

FUNCTION_EXIT;
}

JNIEXPORT void JNICALL
Java_com_telecomyt_videolibrary_VideoClient_SetVideoResolution
(JNIEnv
*env,
jobject jobj, jint
a)
{
VidyoClientRequestConfiguration requestConfiguration;
VidyoUint ret = VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_GET_CONFIGURATION,
                                       &requestConfiguration, sizeof(requestConfiguration));
if (ret != VIDYO_CLIENT_ERROR_OK) {
LOGE("VIDYO_CLIENT_REQUEST_GET_CONFIGURATION returned error!");
return;
}
if(a == 0){
requestConfiguration.
videoPreferences = VIDYO_CLIENT_VIDEO_PREFERENCES_BEST_QUALITY;
}else if(a == 1){
requestConfiguration.
videoPreferences = VIDYO_CLIENT_VIDEO_PREFERENCES_BEST_FRAMERATE;
}else if(a == 2){
requestConfiguration.
videoPreferences = VIDYO_CLIENT_VIDEO_PREFERENCES_BEST_RESOLUTION;
}else if(a == 3){
requestConfiguration.
videoPreferences = VIDYO_CLIENT_VIDEO_PREFERENCES_LIMITED_BANDWIDTH;
}else if(a == 4){
requestConfiguration.
videoPreferences = VIDYO_CLIENT_VIDEO_PREFERENCES_ADVANCED_360p30;
}else if(a == 5){
requestConfiguration.
videoPreferences = VIDYO_CLIENT_VIDEO_PREFERENCES_ADVANCED_450p30;
}else if(a == 6){
requestConfiguration.
videoPreferences = VIDYO_CLIENT_VIDEO_PREFERENCES_ADVANCED_720p15;
}else if(a == 7){
requestConfiguration.
videoPreferences = VIDYO_CLIENT_VIDEO_PREFERENCES_ADVANCED_720p30;
}else if(a == 8){
requestConfiguration.
videoPreferences = VIDYO_CLIENT_VIDEO_PREFERENCES_1080p30;
}else if(a == 9){
requestConfiguration.
videoPreferences = VIDYO_CLIENT_VIDEO_PREFERENCES_ADVANCED_360p15;
}else if(a == 10){
requestConfiguration.
videoPreferences = VIDYO_CLIENT_VIDEO_PREFERENCES_VIDYOROOM_HD1080;
}
ret = VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_SET_CONFIGURATION, &requestConfiguration,
                             sizeof(requestConfiguration));
if (ret != VIDYO_CLIENT_ERROR_OK) {
LOGE("VIDYO_CLIENT_REQUEST_SET_CONFIGURATION returned error!");
}
}




JNIEXPORT void JNICALL Java_com_telecomyt_videolibrary_VideoClient_SendMsgG(JNIEnv *env,jobject jobj, jstring msg){
//jstring m = (*env)->NewStringUTF(env, msg);
const char *m = (*env)->GetStringUTFChars(env, msg, NULL);
VidyoClientInEventGroupChat gChat={0};
strlcpy(gChat.message, m, sizeof(gChat.message));
VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_GROUP_CHAT,&gChat,sizeof(VidyoClientInEventGroupChat));
}

JNIEXPORT void JNICALL Java_com_telecomyt_videolibrary_VideoClient_SetVidyoProxy
(JNIEnv *env,jobject jobj,jstring proxyAddress,jstring proxyPort){
VidyoClientRequestConfiguration requestConfiguration ={0};

VidyoUint ret = VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_GET_CONFIGURATION,
                                       &requestConfiguration,
                                       sizeof(VidyoClientRequestConfiguration));
if (ret != VIDYO_CLIENT_ERROR_OK) {
LOGE("VIDYO_CLIENT_REQUEST_GET_CONFIGURATION returned error!");
//return;
}
const char *address = (*env)->GetStringUTFChars(env, proxyAddress, NULL);
const char *port = (*env)->GetStringUTFChars(env, proxyPort, NULL);

requestConfiguration.proxySettings =  0x0001|0x0008|0x0020|0x0800 ;
VidyoUint8 webProxyAddress[URI_LEN] ={0};//= address;
memcpy(webProxyAddress,address,strlen(address));
memcpy(requestConfiguration.webProxyAddress,webProxyAddress,sizeof(webProxyAddress));

VidyoUint8 webProxyPort[SERVERPORT_SIZE] ={0};//= port;
memcpy(webProxyPort,port,strlen(port));
memcpy(requestConfiguration.webProxyPort,webProxyPort,sizeof(webProxyPort));


LOGE("webProxyAddress === %s",requestConfiguration.webProxyAddress);
LOGE("webProxyPort === %s",requestConfiguration.webProxyPort);


ret = VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_SET_CONFIGURATION, &requestConfiguration,
                             sizeof(requestConfiguration));
if (ret != VIDYO_CLIENT_ERROR_OK) {
LOGE("VIDYO_CLIENT_REQUEST_SET_CONFIGURATION returned error!");
}else{
LOGE("VIDYO_CLIENT_REQUEST_SET_CONFIGURATION returned success!111111111111");
}
}

JNIEXPORT jstring JNICALL Java_com_telecomyt_videolibrary_VideoClient_getUri(JNIEnv *env,jobject jobj,jint pos){
VidyoClientRequestParticipantInfo info;
VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_GET_PARTICIPANT_INFO, &info, sizeof(VidyoClientRequestParticipantInfo));
const char *uriC =info.URI[pos];
jstring js = (*env)->NewStringUTF(env, uriC);
return js;
}

//JNIEXPORT jstringArray JNICALL Java_com_telecomyt_videolibrary_VideoClient_getDetails(JNIEnv *env,jobject jobj){
//VidyoClientRequestParticipantInfo info;
//VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_GET_PARTICIPANT_INFO, &info, sizeof(VidyoClientRequestParticipantInfo));
//const char *uriC =info.URI[1];
//jstring js = (*env)->NewStringUTF(env, uriC);
//return NULL;
//}

JNIEXPORT jboolean JNICALL Java_com_telecomyt_videolibrary_VideoClient_setVideoTop(JNIEnv *env,jobject jobj,jstring uri){
const char *m = (*env)->GetStringUTFChars(env, uri, NULL);
VidyoClientInEventSetParticipantVideoMode mode={0};
strlcpy(mode.uri, m, sizeof(mode.uri));
mode.videoMode=VIDYO_CLIENT_VIDEO_MODE_PINHIGH;
mode.actionType=VIDYO_CLIENT_ACTION_TYPE_PIN;
VidyoUint ret = VidyoClientSendRequest(VIDYO_CLIENT_IN_EVENT_SET_PARTICIPANT_VIDEO_MODE, &mode, sizeof(VidyoClientInEventSetParticipantVideoMode));
if (ret != VIDYO_CLIENT_ERROR_OK) {
LOGE("setVideoTop returned error!");
return JNI_FALSE;
}else{
LOGE("setVideoTop returned success!");
return JNI_TRUE;
}
}
JNIEXPORT jboolean JNICALL Java_com_telecomyt_videolibrary_VideoClient_cancelVideoTop(JNIEnv *env,jobject jobj,jstring uri){
const char *m = (*env)->GetStringUTFChars(env, uri, NULL);
VidyoClientInEventSetParticipantVideoMode mode={0};
strlcpy(mode.uri, m, sizeof(mode.uri));
mode.videoMode=VIDYO_CLIENT_VIDEO_MODE_DOCK;
mode.actionType=VIDYO_CLIENT_ACTION_TYPE_PIN;
VidyoUint ret = VidyoClientSendRequest(VIDYO_CLIENT_IN_EVENT_SET_PARTICIPANT_VIDEO_MODE, &mode, sizeof(VidyoClientInEventSetParticipantVideoMode));
if (ret != VIDYO_CLIENT_ERROR_OK) {
LOGE("cancelVideoTop returned error!");
return JNI_FALSE;
}else{
LOGE("cancelVideoTop returned success!");
return JNI_TRUE;
}
}

JNIEXPORT jstring JNICALL Java_com_telecomyt_videolibrary_VideoClient_getDetail(JNIEnv *env,jobject jobj,jint userId){
VidyoClientRequestParticipantDetailsAt info={0};
info.index=userId;
//const char *userC = (*env)->GetStringUTFChars(env, userId, NULL);
VidyoClientSendRequest(VIDYO_CLIENT_REQUEST_GET_PARTICIPANT_DETAILS_AT, &info, sizeof(VidyoClientRequestParticipantDetailsAt));
VidyoClientRequestParticipantDetails detail = info.details;
const char *uriC =detail.name;
jstring js = (*env)->NewStringUTF(env, uriC);
return js;
}

JNIEXPORT void JNICALL Java_com_telecomyt_videolibrary_VideoClient_fontName(JNIEnv *env,jobject jobj,jstring file){
VidyoClientInEventSetFontFile fontEvent = {0};
//const char *fontFileC = "/data/data/com.telecomyt.videoclient/files/System.vyf";
const char *fontFileC = (*env)->GetStringUTFChars(env, file, NULL);
strncpy(fontEvent.fontFileName, fontFileC, sizeof(fontEvent.fontFileName));
VidyoUint ret =  VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_SET_FONT_FILE, &fontEvent, sizeof(fontEvent));
if (ret != VIDYO_CLIENT_ERROR_OK) {
LOGE("fontName returned error!");
}else{
LOGE("fontName returned success!");
}
}
JNIEXPORT jboolean JNICALL Java_com_telecomyt_videolibrary_VideoClient_setBackgroundColor(JNIEnv *env,jobject jobj,jint red,jint green,jint blue){
VidyoColor colorE = {0};
colorE.red=red;
colorE.green=green;
colorE.blue=blue;
VidyoClientInEventColor colorS = {0};
colorS.color=colorE;
VidyoUint ret =  VidyoClientSendEvent(VIDYO_CLIENT_IN_EVENT_SET_BACKGROUND_COLOR, &colorS, sizeof(VidyoClientInEventColor));
if (ret != VIDYO_CLIENT_ERROR_OK) {
LOGE("setBackgroundColor returned error!");
return JNI_FALSE;
}else{
LOGE("setBackgroundColor returned success!");
return JNI_TRUE;
}
}
