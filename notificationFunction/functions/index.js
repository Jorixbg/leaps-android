'use strict'

const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite(event=>{
    
    const user_id = event.params.user_id;
    const notification_id = event.params.notification_id;
    
    
    if(!event.data.val()){
        
        return console.log('A Notification has been deleted from the database: ', notification_id);
    }
			
        console.log('Notification to : ', user_id);
    
    const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');
	
    return fromUser.then(fromUserResult =>{
        
        const from_user_id = fromUserResult.val().from;
        
       console.log('You have new notification from : ', from_user_id);
		var userSequence;
		
        
        const userQuery = admin.database().ref(`users/${from_user_id}/name`).once('value');        
        const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');
		if(from_user_id > user_id){
			 userSequence = "room-"+user_id+"-"+from_user_id+"-";
		} 
		else {
			 userSequence = "room-"+from_user_id+"-"+user_id+"-";
		}
			const lastMessage = admin.database().ref(`/rooms/${userSequence}/last_message/message`).once('value');
			
			
        return Promise.all([userQuery, deviceToken,lastMessage]).then(result=> {
            
            const userName = result[0].val();
            const token_id = result[1].val();
			const last_message = result[2].val();
			
                
               const payload = {
					notification: {
						title : `${userName}`,
						body : `${last_message}`,
						icon : "default",
						sound : "default"
					}
				};
                
                
               return admin.messaging().sendToDevice(token_id,payload).then(function(response) {
										console.log("Successfully sent message:", response);
									  })
									  .catch(function(error) {
										console.log("Error sending message:");
									  });
            
            
        });

                
                
                
    });

        
});





/*exports.sendNotification = functions.database.ref('/notifications/{user_id}/{notification_id}').onWrite(event=>{
	
	
    const user_id = event.params.user_id;
    const notification_id = event.params.notification_id;
	
	console.log('The User Id is: ',user_id);
	
	if(!event.data.val()){
        
        return console.log('A Notification has been deleted from the database: ', notification_id);
    }
	
	const fromUser = admin.database().ref(`/notifications/${user_id}/${notification_id}`).once('value');
	
	return fromUser.then(fromUserResult =>{
		
		const from_user_id = fromUserResult.val().from;
		
		return console.log('You have new notification from:', from_user_id);
		
		const userQuery = admin.database().ref(`users/${from_user_id}/name`).once('value');
		
		return userQuery.then(userResult =>{
			
			const userName = userResult.val();
			
			
		return console.log('You have new notification from:', userName);
				
			const deviceToken = admin.database().ref(`/users/${user_id}/device_token`).once('value');
	
			return deviceToken.then(result =>{
				
				const token_id = result.val();
				
				
				console.log("TOKEN:", token_id);
				
				const payload = {
					notification: {
						title : "Friend Request",
						body : `${userName} messaged you`,
						icon : "default"
					}
				};
			
				return admin.messaging().sendToDevice(token_id,payload).then(function(response) {
										console.log("Successfully sent message:", response);
									  })
									  .catch(function(error) {
										console.log("Error sending message:");
									  });
			
				
			});
				
			
			
		});
	
		
	});
	
	
	
});*/