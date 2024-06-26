package com.winjay.dlna.cast.dmr;

import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.types.ErrorCode;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.Channel;
import org.fourthline.cling.support.renderingcontrol.AbstractAudioRenderingControl;
import org.fourthline.cling.support.renderingcontrol.RenderingControlErrorCode;
import org.fourthline.cling.support.renderingcontrol.RenderingControlException;

import java.util.Map;

/**
 * @author offbye
 */
public class AudioRenderingControl extends AbstractAudioRenderingControl {
    private static final String TAG = "AudioRenderingControl";

    final private Map<UnsignedIntegerFourBytes, ZxtMediaPlayer> players;

    protected AudioRenderingControl(LastChange lastChange, Map<UnsignedIntegerFourBytes, ZxtMediaPlayer> players) {
        super(lastChange);
        this.players = players;
    }

    protected Map<UnsignedIntegerFourBytes, ZxtMediaPlayer> getPlayers() {
        return players;
    }

    protected ZxtMediaPlayer getInstance(UnsignedIntegerFourBytes instanceId) throws RenderingControlException {
        ZxtMediaPlayer player = getPlayers().get(instanceId);
        if (player == null) {
            throw new RenderingControlException(RenderingControlErrorCode.INVALID_INSTANCE_ID);
        }
        return player;
    }

    protected void checkChannel(String channelName) throws RenderingControlException {
        if (!getChannel(channelName).equals(Channel.Master)) {
            throw new RenderingControlException(ErrorCode.ARGUMENT_VALUE_INVALID, "Unsupported audio channel: " + channelName);
        }
    }

    @Override
    public boolean getMute(UnsignedIntegerFourBytes instanceId, String channelName) throws RenderingControlException {
        checkChannel(channelName);
        return getInstance(instanceId).getVolume() == 0;
    }

    @Override
    public void setMute(UnsignedIntegerFourBytes instanceId, String channelName, boolean desiredMute) throws RenderingControlException {
        checkChannel(channelName);
        LogUtil.d(TAG, "Setting backend mute to: " + desiredMute);
        getInstance(instanceId).setMute(desiredMute);
    }

    @Override
    public UnsignedIntegerTwoBytes getVolume(UnsignedIntegerFourBytes instanceId, String channelName) throws RenderingControlException {
        checkChannel(channelName);
        int vol = (int) (getInstance(instanceId).getVolume() * 100);
        LogUtil.d(TAG, "Getting backend volume: " + vol);
        return new UnsignedIntegerTwoBytes(vol);
    }

    @Override
    public void setVolume(UnsignedIntegerFourBytes instanceId, String channelName, UnsignedIntegerTwoBytes desiredVolume) throws RenderingControlException {
        checkChannel(channelName);
        double vol = desiredVolume.getValue() / 100d;
        LogUtil.d(TAG, "Setting backend volume to: " + vol);
        getInstance(instanceId).setVolume(vol);
    }

    @Override
    protected Channel[] getCurrentChannels() {
        return new Channel[]{
                Channel.Master
        };
    }

    @Override
    public UnsignedIntegerFourBytes[] getCurrentInstanceIds() {
        UnsignedIntegerFourBytes[] ids = new UnsignedIntegerFourBytes[getPlayers().size()];
        int i = 0;
        for (UnsignedIntegerFourBytes id : getPlayers().keySet()) {
            ids[i] = id;
            i++;
        }
        return ids;
    }
}