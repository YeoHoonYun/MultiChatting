package yun.fast.multichatting;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class RoomManger {
    private List<Room> roomList;

    public RoomManger(){
        roomList = Collections.synchronizedList(new ArrayList<>());
    }

    public void roomAdd(Room room){
        this.roomList.add(room);
    }

    public List<Room> getRoomList() {
        return roomList;
    }

    public Room searchRoom(String name){
        for(Room r : roomList){
            if(r.getName().equals(name)){
                return r;
            }
        }
        return null;
    }

    public Room searchRoom(User name){
        for(Room r : roomList){
            for(User user : r.getUserList()){
                if(user == name){
                    return r;
                }
            }
        }
        return null;
    }
    public void roomChat(Room room, String content){
        for (User user1 : room.getUserList()) {
            try {
                user1.getOut().writeUTF(content);
                user1.getOut().flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void exitUser(Room room, User name){
        Iterator<User> iterator = room.getUserList().iterator();
        while(iterator.hasNext()){
            User user = iterator.next();
            if(user == name){
                name.setRoom(false);
                iterator.remove();
            }
        }
        if(!room.getName().equals("Lobby") & room.getUserList().size()==0){
            roomList.remove(room);
        }
        else if(!room.getName().equals("Lobby") & name.getAdmin()){
            room.getUserList().get(0).setAdmin(true);
            String content = "방장이 나갔으므로"+room.getUserList().get(0).getName() + "님이 방장이 되었습니다.";
            this.roomChat(room, content);
        }
        name.setAdmin(false);
    }
}
