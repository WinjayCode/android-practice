
package com.winjay.dlna.cast.dmr;

import android.content.Context;

import com.winjay.dlna.util.LogUtil;

import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.support.lastchange.LastChange;
import org.fourthline.cling.support.model.TransportState;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author offbye
 */
public class ZxtMediaPlayers extends ConcurrentHashMap<UnsignedIntegerFourBytes, ZxtMediaPlayer> {
    private static final String TAG = "ZxtMediaPlayers";
    final protected LastChange avTransportLastChange;
    final protected LastChange renderingControlLastChange;
    private Context mContext;


    public ZxtMediaPlayers(int numberOfPlayers,
                           Context context,
                           LastChange avTransportLastChange,
                           LastChange renderingControlLastChange) {
        super(numberOfPlayers);
        this.mContext = context;
        this.avTransportLastChange = avTransportLastChange;
        this.renderingControlLastChange = renderingControlLastChange;

        for (int i = 0; i < numberOfPlayers; i++) {

            ZxtMediaPlayer player =
                    new ZxtMediaPlayer(
                            new UnsignedIntegerFourBytes(i),
                            mContext,
                            avTransportLastChange,
                            renderingControlLastChange
                    ) {
                        @Override
                        protected void transportStateChanged(TransportState newState) {
                            super.transportStateChanged(newState);
                            if (newState.equals(TransportState.PLAYING)) {
                                onPlay(this);
                            } else if (newState.equals(TransportState.STOPPED)) {
                                onStop(this);
                            }
                        }
                    };
            put(player.getInstanceId(), player);
        }
    }

    protected void onPlay(ZxtMediaPlayer player) {
        LogUtil.d(TAG, "Player is playing: " + player.getInstanceId());
    }

    protected void onStop(ZxtMediaPlayer player) {
        LogUtil.d(TAG, "Player is stopping: " + player.getInstanceId());
    }
}
