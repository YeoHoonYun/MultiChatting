package yun.fast.multichatting;

import java.io.IOException;

public class ChatMethod {
    private RoomManger roomManger;

    public ChatMethod(RoomManger roomManger) {
        this.roomManger = roomManger;
    }

    public void lobbyToRoom(Room room, User user, String title){
        roomManger.roomAdd(room);
        roomManger.exitUser(roomManger.searchRoom("Lobby"), user);
        room.userAdd(user);
        try {
            user.getOut().writeUTF(title + "방이 생성되었습니다.");
            user.getOut().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setAdmin(true);
        user.setRoom(true);
    }
    public void joinToRoom(User user,String title){
        Room room = roomManger.searchRoom(title);
        roomManger.exitUser(roomManger.searchRoom("Lobby"), user);
        try {
            user.getOut().writeUTF(room.getName() + "방에 접속하였습니다.");
            user.getOut().flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        user.setRoom(true);
        room.userAdd(user);
    }
}
