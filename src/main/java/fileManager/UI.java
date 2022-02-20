package fileManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class UI extends JFrame {
    private JPanel catalogPannel = new JPanel();    // Главная панель
    private JList filesList = new JList();          // Отображение списка файлов
    private JScrollPane filesScroll = new JScrollPane(filesList);
    private JPanel buttonPanel = new JPanel();
    private JButton addButton = new JButton("Создать папку");      // Кнопка
    private JButton backButton = new JButton("Назал");             // Кнопка
    private JButton deletButton = new JButton("Удалить");          // Кнопка
    private JButton renameButton = new JButton("Переименовать");   // Кнопка
    private ArrayList<String> dirscash = new ArrayList<>();            // Лист-путь

    public UI() {
        super("Проводник");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);                // Приложение закрывается при нажатии на крестик
        setResizable(true);                                            // Окно можно растягивать
        catalogPannel.setLayout(new BorderLayout(5, 5));   // Менеджер размещения объектов
        catalogPannel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));         // Расстояние между объектами
        buttonPanel.setLayout(new GridLayout(1, 4, 5, 5));
        JDialog newDirDialog = new JDialog(UI.this, "Создание папки", true);        // Открывается новое диалоговое окно "Создание папки"
        JPanel newDirPanel = new JPanel();
        newDirDialog.add(newDirPanel);
        File discs[] = File.listRoots();                              // Открывается дерево дисков
        filesScroll.setPreferredSize(new Dimension(400, 500));             // Размер окна
        filesList.setListData(discs);
        filesList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);     // Позволяем выделять несколько объектов

        filesList.addMouseListener(new MouseListener() {                                // Создаём слушатель для листа
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
                    DefaultListModel model = new DefaultListModel();
                    String selectedObject = filesList.getSelectedValue().toString();
                    String fullPath = toFullPath(dirscash);                                 // Передаём в метод путь для склейки
                    File selectedFile;
                    if (dirscash.size() > 1) {                                              // Проверям нахождение файла для склейки с текущим объектом
                        selectedFile = new File(fullPath, selectedObject);
                    } else {
                        selectedFile = new File(fullPath + selectedObject);
                    }

                    if (selectedFile.isDirectory()) {                                       // Проверяем является ли то, куда мы перешли директорией (т.е. можно ли переходить дальше)
                        String[] rootStr = selectedFile.list();
                        for (String output : rootStr) {
                            File checkObject = new File(selectedFile.getPath(), output);
                            if(!checkObject.isHidden()) {                                   // Проверяем скрытый объект или нет, нужно ли его отображать
                                if (checkObject.isDirectory()) {
                                    model.addElement(output);
                                } else {
                                    model.addElement("Файл " + output);
                                }
                            }
                        }
                        dirscash.add(selectedObject);
                        filesList.setModel(model);
                    }
                }


            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        backButton.addActionListener(new ActionListener() {     // Добавляем слушатель кнопки "Назад"
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dirscash.size() > 1) {                      // Проверяем находимся ли мы больше чем на уровне дисков
                    dirscash.remove(dirscash.size() - 1);
                    String backDir = toFullPath(dirscash);
                    String[] objects = new File(backDir).list();
                    DefaultListModel backRootModel = new DefaultListModel();

                    for (String output : objects) {
                        File checkFile = new File(backDir, output);
                        if (!checkFile.isHidden()) {
                            if (checkFile.isDirectory()) {
                                backRootModel.addElement(output);
                            } else {
                                backRootModel.addElement("Файл " + output);
                            }
                        }
                    }
                    filesList.setModel(backRootModel);
                } else {
                    dirscash.removeAll(dirscash);
                    filesList.setListData(discs);
                }
            }
        });

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
               if (!dirscash.isEmpty()) {
                   String currentPath;
                   File newFolder;
                   CreateNewFolderJDialog newFolderJDialog = new CreateNewFolderJDialog(UI.this);

                   if (newFolderJDialog.getReady()) {
                       currentPath = toFullPath(dirscash);
                       newFolder = new File(currentPath, newFolderJDialog.getNewName());
                       if(!newFolder.exists()) {
                           newFolder.mkdir();

                           File updateDir = new File(currentPath);
                           String updateMas[] = updateDir.list();
                           DefaultListModel updateModel = new DefaultListModel();
                           for (String u : updateMas) {
                               File check = new File(updateDir.getPath(), u);
                               if (!check.isHidden()) {
                                   if (check.isDirectory()) {
                                       updateModel.addElement(u);
                                   } else {
                                       updateModel.addElement("Файл " + u);
                                   }
                               }
                           }
                           filesList.setModel(updateModel);
                       }
                   }
               }
            }
        });

        deletButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedObject = filesList.getSelectedValue().toString();
                String currentPath = toFullPath(dirscash);
                if(!selectedObject.isEmpty()) {

                    deleteDir(new File(currentPath, selectedObject));

                    File updateDir = new File(currentPath);
                    String updateMas[] = updateDir.list();
                    DefaultListModel updateModel = new DefaultListModel();
                    for (String u : updateMas) {
                        File check = new File(updateDir.getPath(), u);
                        if (!check.isHidden()) {
                            if (check.isDirectory()) {
                                updateModel.addElement(u);
                            } else {
                                updateModel.addElement("Файл " + u);
                            }
                        }
                    }
                    filesList.setModel(updateModel);
                }
            }
        });

        renameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!dirscash.isEmpty() && filesList.getSelectedValue() != null) {
                    String selectedObject = filesList.getSelectedValue().toString();
                    String currentPath = toFullPath(dirscash);
                    RenameJDialog renamer = new RenameJDialog(UI.this);
                    if (renamer.getReady()) {
                        File renameFile = new File(currentPath, selectedObject);
                        renameFile.renameTo(new File(currentPath, renamer.getName()));

                        File updateDir = new File(currentPath);
                        String updateMas[] = updateDir.list();
                        DefaultListModel updateModel = new DefaultListModel();
                        for (String u : updateMas) {
                            File check = new File(updateDir.getPath(), u);
                            if (!check.isHidden()) {
                                if (check.isDirectory()) {
                                    updateModel.addElement(u);
                                } else {
                                    updateModel.addElement("Файл " + u);
                                }
                            }
                        }
                        filesList.setModel(updateModel);

                    }
                }
            }
        });

        getContentPane().add(catalogPannel);                   // Добавляем панель
        setSize(800, 600);                        // Устанавливаем размеры
        setLocationRelativeTo(null);                           // Центрируем окно
        setVisible(true);                                      // Устанавливаем видимость

        buttonPanel.add(backButton);                           // Добавляем кнопку "НАЗАД"
        buttonPanel.add(addButton);
        buttonPanel.add(deletButton);                          // Добавляем кнопку "УДАЛИТЬ"
        buttonPanel.add(renameButton);                         // Добавляем кнопку "ПЕРЕИМЕНОВАТЬ"
        catalogPannel.setLayout(new BorderLayout());
        catalogPannel.add(filesScroll, BorderLayout.CENTER);
        catalogPannel.add(buttonPanel, BorderLayout.SOUTH);    // Добавляем анель с кнопками

    }

    public String toFullPath(List<String> file) {               // Метод для склейки пути к объекту
        StringBuilder listPart = new StringBuilder();
        for(String output : file) {
            listPart.append(output);
        }
        return listPart.toString();
    }

    public void deleteDir(File file) {
        File[] objects = file.listFiles();
        if (objects != null) {
            for (File f : objects) {
                deleteDir(f);
            }
        }
        file.delete();
    }



}
