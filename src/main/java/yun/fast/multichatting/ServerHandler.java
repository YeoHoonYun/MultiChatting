package yun.fast.multichatting;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerHandler extends Thread{
    private Socket socket = null;
    private RoomManger roomManger;

    public ServerHandler(Socket socket, RoomManger roomManger) {
        this.socket = socket;this.roomManger = roomManger;
    }

    @Override
    public void run() {
        ChatMethod chatMethod = new ChatMethod(roomManger);
        DataOutputStream out = null;
        DataInputStream in = null;
        User user = null;
        Room room;
        try {
            user = new User(socket);
            roomManger.searchRoom("Lobby").userAdd(user);
            user.setIn(new DataInputStream(socket.getInputStream()));
            user.setOut(new DataOutputStream(socket.getOutputStream()));

            in = user.getIn();
            out = user.getOut();

            out.writeUTF("별명을 입력하세요 : ");
            out.flush();
            user.setName(in.readUTF());
            out.writeUTF("\""+user.getName() + "\"로 설정 되었습니다.");
            out.flush();
            while(true){
                if(!user.isRoom()) {
                    while(true) {
                        out.writeUTF("내용(/help 도움말 | /quit 종료) : ");
                        String content = in.readUTF();
                        if (content.indexOf("/help") == 0) {
                            out.writeUTF("--------명령어 도움말--------");
                            out.flush();
                            out.writeUTF("/help : 명령어 보기");
                            out.flush();
                            out.writeUTF("/quit : 프로그램 종료하기");
                            out.flush();
                            out.writeUTF("/create 방이름 : 방 생성 하기");
                            out.flush();
                            out.writeUTF("/join 방이름 : 방 참여 하기");
                            out.flush();
                            out.writeUTF("/list : 방 리스트 출력하기");
                            out.flush();
                            out.writeUTF("-----------------------------");
                            out.flush();
                        } else if (content.indexOf("/create") == 0) {
                            try{
                                String title = content.split(" ")[1];
                                room = new Room(title);
                                chatMethod.lobbyToRoom(room, user, title);
                                break;

                            }catch (Exception e){
                                out.writeUTF("잘못된 값입니다. 방제목을 입력하세요.");
                                break;
                            }
                        } else if (content.indexOf("/join") == 0) {
                            try{
                                String title = content.split(" ")[1];
                                chatMethod.joinToRoom(user, title);
                                break;
                            }catch (Exception e){
                                out.writeUTF("잘못된 값입니다. 방제목을 입력하세요.");
                                break;
                            }
                        } else if (content.indexOf("/list") == 0) {
                            int i = 0;
                            for (Room room1 : roomManger.getRoomList()) {
                                out.writeUTF(i + " : " + room1.getName());
                                i++;
                            }

                        }else if (content.indexOf("/quit") == 0) {
                            out.writeUTF("프로그램을 종료합니다.");
                            in.close();
                            out.close();
                            socket.close();
                        }
                        else {
                            roomManger.roomChat(roomManger.searchRoom("Lobby"), user.getName() + " : " + content);
                        }
                    }
                }
                else if(user.isRoom()) {
                    room = roomManger.searchRoom(user);
                    String name = room.getName();
                    String text = name + "방에 "+ user.getName()+ "가 입장하였습니다.";
                    roomManger.roomChat(room, text);
                    while (true) {
                        if(!user.isRoom()){
                            out.writeUTF("현재 방안에 없습니다.");
                            break;
                        }
                        out.writeUTF("내용(/help 도움말) : ");
                        String content = in.readUTF();
                        if (content.indexOf("/help") == 0) {
                            out.writeUTF("--------명령어 도움말--------");
                            out.writeUTF("/users : 사용자 리스트");
                            out.writeUTF("/exit : 방 나가기");
                            if(user.getAdmin()){
                                out.writeUTF("/kick : 사용자 강퇴");
                            }
                        } else if (content.indexOf("/users") == 0) {
                            int i = 0;
                            out.writeUTF(name + "방 사용자 List");
                            for (User u : room.getUserList()) {
                                out.writeUTF(i + " : " + u.getName());
                                i++;
                            }
                        } else if (content.indexOf("/exit") == 0) {
                            String exitText = user.getName() + "가 "+room.getName()+"에서 나옵니다.";
                            roomManger.roomChat(room, exitText);
                            roomManger.exitUser(room, user);
                            roomManger.searchRoom("Lobby").userAdd(user);
                            break;
                        } else if (user.getAdmin() & content.indexOf("/kick") == 0) {
                            out.writeUTF("강퇴할 사람의 번호를 입력하세요.");
                            out.writeUTF(name + "방 사용자 List");
                            int i = 0;
                            for (User u : room.getUserList()) {
                                out.writeUTF(i + " : " + u.getName());
                                i++;
                            }
                            out.writeUTF("강퇴시킬 사람의 번호 : ");
                            int kickText = Integer.parseInt(in.readUTF());
                            try {
                                User user1 = room.getUserList().get(kickText);
                                out.writeUTF(user1.getName() + "님을 강퇴하였습니다.");
                                String exitText = user1.getName() + "가 " + room.getName() + "에서 강퇴당했습니다.";
                                roomManger.roomChat(room, exitText);
                                roomManger.exitUser(room, user1);
                            }catch (Exception e){
                                out.writeUTF("잘못된 값입니다. 방제목을 입력하세요.");
                            }
                        }
                        else {
                            if(user.isRoom()) {
                                roomManger.roomChat(room, user.getName() + " : " + content);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
