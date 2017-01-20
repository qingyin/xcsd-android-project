package com.tuxing.sdk.manager;

import com.tuxing.sdk.db.entity.RevokedMessage;

import java.util.List;

/**
 * Created by Alan on 2015/9/10.
 */
public interface MessageManager extends BaseManager{
    RevokedMessage getRevokedMessage(String msgId);

    void deleteRevokedMessage(String msgId);

    void saveRevokedMessage(RevokedMessage revokedMessage);

    List<RevokedMessage> getAllRevokedMessage();
}
