/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tuxing.app.easemob.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import com.easemob.chat.EMMessage;
import com.tuxing.app.R;
import com.tuxing.app.domain.Conversation;
import com.tuxing.app.easemob.util.SmileUtils;
import com.tuxing.app.view.RoundImageView;

import java.util.List;

/**
 * 显示所有聊天记录adpater
 *
 */
public class ChatAllHistoryAdapter extends ArrayAdapter<Conversation> {

	private static final String TAG = "ChatAllHistoryAdapter";
	private LayoutInflater inflater;
	public Context mContext;

	public ChatAllHistoryAdapter(Context context, int textViewResourceId, List<Conversation> conversations) {
		super(context, textViewResourceId, conversations);

		inflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.row_chat_history, parent, false);
		}

		ViewHolder holder = (ViewHolder) convertView.getTag();
		if (holder == null) {
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.isNew = (ImageView) convertView.findViewById(R.id.conversation_isnew);
			holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
			holder.message = (TextView) convertView.findViewById(R.id.message);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.avatar = (RoundImageView) convertView.findViewById(R.id.avatar);
			holder.msgState = convertView.findViewById(R.id.msg_state);
			holder.list_item_layout = (RelativeLayout) convertView.findViewById(R.id.list_item_layout);
			convertView.setTag(holder);
		}
		// 获取与此用户/群组的会话
		final Conversation conversation = getItem(position);

		holder.name.setText(conversation.name);
		holder.message.setText(SmileUtils.getSmiledText(getContext(),
                conversation.desc), BufferType.SPANNABLE);
		holder.avatar.setImageUrl(conversation.avatar, conversation.defaultAvatar);

		holder.time.setText(conversation.showTime);
		if(conversation.msgStatus == EMMessage.Status.FAIL){
			holder.msgState.setVisibility(View.VISIBLE);
		}else{
			holder.msgState.setVisibility(View.GONE);
		}

		if (conversation.count > 0) {
			// 显示与此用户的消息未读数
            if(conversation.type == Conversation.TYPE.LEARN_GARDEN ||
                    conversation.type == Conversation.TYPE.CHECK_IN){
                holder.isNew.setVisibility(View.VISIBLE);
                holder.unreadLabel.setVisibility(View.INVISIBLE);

            }else {
            	if(conversation.count >= 10){
            		 holder.unreadLabel.setTextSize(8);
            	}else{
            		holder.unreadLabel.setTextSize(10);
            	}
                holder.unreadLabel.setText(conversation.count > 99 ? "···" : String.valueOf(conversation.count));
                holder.unreadLabel.setVisibility(View.VISIBLE);
                holder.isNew.setVisibility(View.GONE);
            }
		} else {
            holder.isNew.setVisibility(View.INVISIBLE);
			holder.unreadLabel.setVisibility(View.INVISIBLE);
		}

		return convertView;
	}

	private static class ViewHolder {
		/** 和谁的聊天记录 */
		TextView name;
		/** 消息未读数 */
		TextView unreadLabel;
        /** 是否有新的内容 */
        ImageView isNew;
		/** 最后一条消息的内容 */
		TextView message;
		/** 最后一条消息的时间 */
		TextView time;
		/** 用户头像 */
		RoundImageView  avatar;
		/** 最后一条消息的发送状态 */
		View msgState;
		/** 整个list中每一行总布局 */
		RelativeLayout list_item_layout;

	}
}
