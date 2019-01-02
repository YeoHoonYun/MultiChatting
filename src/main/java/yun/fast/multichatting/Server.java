package yun.fast.multichatting;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private int port;
    RoomManger roomManger;

    public Server(int port) {
        this.port = port;
        roomManger = new RoomManger();

    }

    public void start(){
        ServerSocket serverSocket = null;
        roomManger.roomAdd(new Room("Lobby"));
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("연결을 대기합니다.");
            while(true) {
                Socket socket = serverSocket.accept();
                System.out.println("클라이언트가 연결되었습니다.");
                ServerHandler serverHandler = new ServerHandler(socket, roomManger);
                serverHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8000);
        server.start();
    }
}
