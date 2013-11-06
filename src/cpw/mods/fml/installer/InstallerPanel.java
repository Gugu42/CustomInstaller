package cpw.mods.fml.installer;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

import com.google.common.base.Throwables;

public class InstallerPanel extends JPanel
{
    private static final long serialVersionUID = 1L;
    private File targetDir;
    private JTextField selectedDirText;
    private JLabel infoLabel;
    private JButton sponsorButton;
    private JDialog dialog;
    // private JLabel sponsorLogo;
    private JPanel sponsorPanel;
    private JPanel fileEntryPanel;

    private class FileSelectAction extends AbstractAction
    {
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e)
        {
            JFileChooser dirChooser = new JFileChooser();
            dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            dirChooser.setFileHidingEnabled(false);
            dirChooser.ensureFileIsVisible(targetDir);
            dirChooser.setSelectedFile(targetDir);
            int response = dirChooser.showOpenDialog(InstallerPanel.this);
            switch(response)
            {
            case JFileChooser.APPROVE_OPTION:
                targetDir = dirChooser.getSelectedFile();
                updateFilePath();
                break;
            default:
                break;
            }
        }
    }

    public InstallerPanel(File targetDir, boolean updateMode)
    {
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        BufferedImage image;
        try
        {
            image = ImageIO.read(SimpleInstaller.class.getResourceAsStream(VersionInfo.getLogoFileName()));
        }
        catch(IOException e)
        {
            throw Throwables.propagate(e);
        }

        JPanel logoSplash = new JPanel();
        logoSplash.setLayout(new BoxLayout(logoSplash, BoxLayout.Y_AXIS));
        ImageIcon icon = new ImageIcon(image);
        JLabel logoLabel = new JLabel(icon);
        logoLabel.setAlignmentX(CENTER_ALIGNMENT);
        logoLabel.setAlignmentY(CENTER_ALIGNMENT);
        logoLabel.setSize(image.getWidth(), image.getHeight());
        logoSplash.add(logoLabel);
        String welcome = updateMode ? "Update avaible :" : RemoteInfo.getWelcomeMessage();
        JLabel tag = new JLabel(welcome);
        tag.setAlignmentX(CENTER_ALIGNMENT);
        tag.setAlignmentY(CENTER_ALIGNMENT);
        logoSplash.add(tag);
        tag = new JLabel(RemoteInfo.getNameAndVersion());
        tag.setAlignmentX(CENTER_ALIGNMENT);
        tag.setAlignmentY(CENTER_ALIGNMENT);
        logoSplash.add(tag);

        logoSplash.setAlignmentX(CENTER_ALIGNMENT);
        logoSplash.setAlignmentY(TOP_ALIGNMENT);
        this.add(logoSplash);

        sponsorPanel = new JPanel();
        sponsorPanel.setLayout(new BoxLayout(sponsorPanel, BoxLayout.X_AXIS));
        sponsorPanel.setAlignmentX(CENTER_ALIGNMENT);
        sponsorPanel.setAlignmentY(CENTER_ALIGNMENT);

        // sponsorLogo = new JLabel();
        // sponsorLogo.setSize(50, 20);
        // sponsorLogo.setAlignmentX(CENTER_ALIGNMENT);
        // sponsorLogo.setAlignmentY(CENTER_ALIGNMENT);
        // sponsorPanel.add(sponsorLogo);

        sponsorButton = new JButton();
        sponsorButton.setAlignmentX(CENTER_ALIGNMENT);
        sponsorButton.setAlignmentY(CENTER_ALIGNMENT);
        sponsorButton.setBorderPainted(false);
        sponsorButton.setOpaque(false);
        sponsorButton.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                try
                {
                    Desktop.getDesktop().browse(new URI(sponsorButton.getToolTipText()));
                    EventQueue.invokeLater(new Runnable()
                    {
                        @Override
                        public void run()
                        {
                            InstallerPanel.this.dialog.toFront();
                            InstallerPanel.this.dialog.requestFocus();
                        }
                    });
                }
                catch(Exception ex)
                {
                    JOptionPane.showMessageDialog(InstallerPanel.this, "An error occurred launching the browser", "Error launching browser", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        sponsorPanel.add(sponsorButton);

        this.add(sponsorPanel);

        new ButtonGroup();

        JPanel choicePanel = new JPanel();
        choicePanel.setLayout(new BoxLayout(choicePanel, BoxLayout.Y_AXIS));

        choicePanel.setAlignmentX(RIGHT_ALIGNMENT);
        choicePanel.setAlignmentY(CENTER_ALIGNMENT);
        add(choicePanel);
        JPanel entryPanel = new JPanel();
        entryPanel.setLayout(new BoxLayout(entryPanel, BoxLayout.X_AXIS));

        this.targetDir = targetDir;
        selectedDirText = new JTextField();
        selectedDirText.setEditable(false);
        selectedDirText.setToolTipText("Path to minecraft");
        selectedDirText.setColumns(30);
        entryPanel.add(selectedDirText);
        JButton dirSelect = new JButton();
        dirSelect.setAction(new FileSelectAction());
        dirSelect.setText("...");
        dirSelect.setToolTipText("Select an alternative minecraft directory");
        entryPanel.add(dirSelect);

        entryPanel.setAlignmentX(LEFT_ALIGNMENT);
        entryPanel.setAlignmentY(TOP_ALIGNMENT);

        infoLabel = new JLabel();
        infoLabel.setHorizontalTextPosition(JLabel.LEFT);
        infoLabel.setVerticalTextPosition(JLabel.TOP);
        infoLabel.setAlignmentX(LEFT_ALIGNMENT);
        infoLabel.setAlignmentY(TOP_ALIGNMENT);
        infoLabel.setForeground(Color.RED);
        infoLabel.setVisible(false);

        fileEntryPanel = new JPanel();
        fileEntryPanel.setLayout(new BoxLayout(fileEntryPanel, BoxLayout.Y_AXIS));
        fileEntryPanel.add(infoLabel);
        fileEntryPanel.add(Box.createVerticalGlue());
        fileEntryPanel.add(entryPanel);
        fileEntryPanel.setAlignmentX(CENTER_ALIGNMENT);
        fileEntryPanel.setAlignmentY(TOP_ALIGNMENT);
        this.add(fileEntryPanel);
        updateFilePath();
    }

    private void updateFilePath()
    {
        try
        {
            targetDir = targetDir.getCanonicalFile();
            selectedDirText.setText(targetDir.getPath());
        }
        catch(IOException e)
        {

        }

        InstallerAction action = InstallerAction.CLIENT;
        boolean valid = action.isPathValid(targetDir);

        String sponsorMessage = action.getSponsorMessage();
        if(sponsorMessage != null)
        {
            sponsorButton.setText(sponsorMessage);
            sponsorButton.setToolTipText(action.getSponsorURL());
            if(action.getSponsorLogo() != null)
            {
                sponsorButton.setIcon(action.getSponsorLogo());
            }
            else
            {
                sponsorButton.setIcon(null);
            }
            sponsorPanel.setVisible(true);
        }
        else
        {
            sponsorPanel.setVisible(false);
        }
        if(valid)
        {
            selectedDirText.setForeground(Color.BLACK);
            infoLabel.setVisible(false);
            fileEntryPanel.setBorder(null);
        }
        else
        {
            selectedDirText.setForeground(Color.RED);
            fileEntryPanel.setBorder(new LineBorder(Color.RED));
            infoLabel.setText("<html>" + action.getFileError(targetDir) + "</html>");
            infoLabel.setVisible(true);
        }
        if(dialog != null)
        {
            dialog.invalidate();
            dialog.pack();
        }
    }

    public void run(boolean updateMode)
    {
        String buttonName = updateMode ? "Update" : "Install";
        JOptionPane optionPane = new JOptionPane(this, JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null, new String[] {buttonName, "Cancel"}, "defaut");

        Frame emptyFrame = new Frame("Mod system installer");
        emptyFrame.setUndecorated(true);
        emptyFrame.setVisible(true);
        emptyFrame.setLocationRelativeTo(null);
        String frameName = updateMode ? "Mod System updater" : "Mod system installer";
        dialog = optionPane.createDialog(emptyFrame, frameName);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);

        String result = (String)(optionPane.getValue() != null ? optionPane.getValue() : "error");
        if(result.equals("Install") || result.equals("Update"))
        {
            InstallerAction action = InstallerAction.CLIENT;
            if(action.run(targetDir, updateMode))
            {
                JOptionPane.showMessageDialog(null, action.getSuccessMessage(), "Complete", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        dialog.dispose();
        emptyFrame.dispose();
    }
}