'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite(event => {

    const user_id = event.params.user_id;
    const notification_id = event.params.notification_id;


    if (!event.data.val()) {

        return console.log('A Notification has been deleted from the database: ', notification_id);
    }

    console.log('Notification to : ', user_id);

    const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');

    return fromUser.then(fromUserResult => {

        const from_user_id = fromUserResult.val().from;

        console.log('You have new notification from : ', from_user_id);
        var userSequence;
		var wichUser;
		var wichCurrentUser;
		var seen;
		var mobile;
		
		if (from_user_id > user_id) {
            userSequence = "room-" + user_id + "-" + from_user_id + "-";
			wichUser = "user2";
			wichCurrentUser = "user1";
			seen = "seenUser1"
        } else {
            userSequence = "room-" + from_user_id + "-" + user_id + "-";
			wichUser = "user1";
			wichCurrentUser = "user2";
			seen = "seenUser2"
        }
		
        const userQuery = admin.database().ref(`/rooms/${userSequence}/${wichUser}/name`).once('value');
        const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');
        
        const lastMessage = admin.database().ref(`/rooms/${userSequence}/last_message/message`).once('value');
		const seenMessage = admin.database().ref(`/rooms/${userSequence}/last_message/${seen}`).once('value');
		const userImage = admin.database().ref(`/rooms/${userSequence}/${wichUser}/image`).once('value');
		const currentUserSeen = admin.database().ref(`/rooms/${userSequence}/last_message/image`).once('value');
		const mobileOs = admin.database().ref(`/users/${user_id}/os`).once('value');
		
        return Promise.all([userQuery, deviceToken, lastMessage,userImage,seenMessage,mobileOs]).then(result => {
			
            const userName = result[0].val();
            const token_id = result[1].val();
            const last_message = result[2].val();
            const user_image = result[3].val();
			const seen_user = result[4].val();
			const deviceOs = result[5].val();
			
			var payload;
			
		
		
		if(deviceOs === 'android'){
			payload = { 
			  data: {
					extra_information: "clicked",
					room_id: userSequence,
					userid:from_user_id,
					user_name:`${userName}`,
					userimage:`${user_image}`,
					seenuser :`${seen_user}`,
					body: `${last_message}`,
					click_action: "EVENTACTIVITY"
				   
				}			 
			};
		}else {
			payload = { 
				notification: {
					title: `${userName}`,
					body: `${last_message}`
				  },
				  data: {
						extra_information: "clicked",
						room_id: userSequence,
						userid:from_user_id,
						user_name:`${userName}`,
						userimage:`${user_image}`,
						seenuser :`${seen_user}`,
						body: `${last_message}`,
						click_action: "EVENTACTIVITY"
					   
					}		 
				};
		}


            return admin.messaging().sendToDevice(token_id, payload).then(function(response) {
                    console.log("Successfully sent to:", token_id);
                    console.log("Successfully sent message:", `${last_message}`);
                })
                .catch(function(error) {
                    console.log("Error sending message:",error);
                });


        });




    });


});
