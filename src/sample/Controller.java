package sample;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import Gauss.GaussSolution;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Controller {

    @FXML
    private TextField textFunc;

    @FXML
    private TextField sIntegralOne;

    @FXML
    private TextField eIntegralOne;

    @FXML
    private TextField nx;

    @FXML
    private TextField sIntegralTwo;

    @FXML
    private TextField eIntegralTwo;

    @FXML
    private TextField ny;

    @FXML
    private TextField sIntegralThree;

    @FXML
    private TextField eIntegralThree;

    @FXML
    private TextField nz;

    @FXML
    private Button startSolution;

    @FXML
    private Label resultIntegralOne;

    @FXML
    private TextField ipServer;

    @FXML
    private TextField portServer;

    @FXML
    private ListView<String> listServer;

    @FXML
    private Button addServerList;

    @FXML
    private Button startSolutionNet;

    @FXML
    private Label resultIntegralNet;

    @FXML
    private Label resultTimeNet;

    @FXML
    private Label resultTimeOne;

    private List<String> serverIpList;

    private List<String> serverPortList;

    @FXML
    void initialize() {
        serverIpList = new ArrayList<>();
        serverPortList = new ArrayList<>();

        startSolution.setOnAction(event -> {
            double resultIntegralO = 0;

            long startTime = System.currentTimeMillis();

            GaussSolution solution = new GaussSolution(Double.parseDouble(nx.getText()), Double.parseDouble(ny.getText()),
                    Double.parseDouble(nz.getText()), Double.parseDouble(sIntegralOne.getText()),
                    Double.parseDouble(eIntegralOne.getText()), Double.parseDouble(sIntegralTwo.getText()),
                    Double.parseDouble(eIntegralTwo.getText()), Double.parseDouble(sIntegralThree.getText()),
                    Double.parseDouble(eIntegralThree.getText()), textFunc.getText());
            resultIntegralO = solution.Solution();

            long endTime = System.currentTimeMillis();

            resultIntegralOne.setText(String.valueOf(resultIntegralO));
            double time = (endTime - startTime)/1000.0;
            resultTimeOne.setText(time + " секунд(ы)");
        });

        addServerList.setOnAction(event -> {
            if (serverIsOnline(ipServer.getText(), Integer.parseInt(portServer.getText()))) {
                listServer.getItems().add(ipServer.getText() + ":" + portServer.getText());
                serverIpList.add(ipServer.getText());
                serverPortList.add(portServer.getText());
                ipServer.clear();
                portServer.clear();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Пробное соединение");
                alert.setHeaderText("Результат");
                alert.setContentText("Соединение не прошло или сервер не запущен!");

                alert.showAndWait();
            }
        });

        startSolutionNet.setOnAction(event -> {
            Solution();
        });
    }

    private boolean serverIsOnline(String serverIp, int serverPort) {
        boolean result = true;

        try {
            InetSocketAddress address = new InetSocketAddress(serverIp, serverPort);
            Socket socket = new Socket();
            socket.connect(address, 2000);
            socket.close();
        } catch (Exception e) {
            result = false;
        }

        return result;
    }

    private void Solution() {
        Socket[] serverSockets = new Socket[listServer.getItems().size()];

        double Nx = Double.parseDouble(nx.getText());
        double Ny = Double.parseDouble(ny.getText());
        double Nz = Double.parseDouble(nz.getText());

        double sI1 = Double.parseDouble(sIntegralOne.getText());
        double eI1 = Double.parseDouble(eIntegralOne.getText());

        double sI2 = Double.parseDouble(sIntegralTwo.getText());
        double eI2 = Double.parseDouble(eIntegralTwo.getText());

        double sI3 = Double.parseDouble(sIntegralThree.getText());
        double eI3 = Double.parseDouble(eIntegralThree.getText());

        double hx = (eI1 - sI1) / Nx;
        double hy = (eI2 - sI2) / Ny;
        double hz = (eI3 - sI3) / Nz;

        int countServers = listServer.getItems().size();
        double result = 0;

        long startTime = System.currentTimeMillis();

        double tempHx = sI1;
        while (tempHx < eI1) {
            double tempHy = sI2;
            while (tempHy < eI2) {
                double tempHz = sI3;
                while (tempHz < eI3) {
                    double temp = tempHz;
                    for (int i = 0; i < countServers; i++) {
                        try {
                            if (temp < eI3) {
                                serverSockets[i] = new Socket();
                                serverSockets[i].connect(new InetSocketAddress(serverIpList.get(i), Integer.parseInt(serverPortList.get(i))), 2000);

                                String funcParam = "";
                                funcParam += tempHx + ";" + tempHy + ";" + temp + ";";
                                funcParam += hx + ";" + hy + ";" + hz + ";" + textFunc.getText() + ";";
                                temp += 2 * hz;

                                OutputStream out = serverSockets[i].getOutputStream();
                                out.write(funcParam.getBytes());
                                out.flush();
                            } else {
                                break;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    temp = tempHz;
                    for (int i = 0; i < countServers; i++) {
                        try {
                            if (temp < eI3) {
                                InputStream in = serverSockets[i].getInputStream();

                                byte[] data = new byte[1024];
                                int readBytes = in.read(data);

                                if (readBytes > 0) {
                                    String str = new String(data, 0, readBytes);
                                    result += Double.parseDouble(str);
                                }
                                serverSockets[i].close();
                                temp += 2 * hz;
                            } else {
                                break;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    tempHz += countServers * 2 * hz;
                }
                tempHy += 2 * hy;
            }
            tempHx += 2 * hx;
        }

        long endTime = System.currentTimeMillis();
        double time = (endTime-startTime)/1000.0;

        resultIntegralNet.setText(String.valueOf(result));
        resultTimeNet.setText(time + " секунд(ы)");
    }
}

