# Chat's Persistance
We are going to talk about the persistance of the chat. Just remember the following one simple principle.

1. If boths are disconnected, delete the chat
We can do it with handling the disconnection event. If one user is disconnected and you got the event, make the value of the key "chatRoom:{roomId}:presence:{memberId}" "OFFLINE" with 10 minutes TTL.

And do this as well. If user connects again, delete the TTL. If user doesn't, Just let them delete after 10 minutes and get the deletion event from redis.

Finally, then check if the another user is still connected by doing like "SCAN 0 MATCH chatRoom:{roomId}:presence:* COUNT 1". If there is no match, it means both users have gone. Delete all over the chat related resources.

Got it?



