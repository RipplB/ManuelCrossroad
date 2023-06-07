package manuel;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;


public class IntersectModelView extends GridWorldView {
    private final Logger logger = Logger.getLogger("project."+Env.class.getName());

    public IntersectModelView(GridWorldModel model, String title, int windowSize) {
        super(model, title, windowSize);
        setVisible(true);
        repaint();
    }

   @Override
    public void initComponents(int width) {
        super.initComponents(width);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setSize(10,10);
        JButton butt1 = new JButton("1");
        butt1.setBounds(0,0,2,2);
       JButton butt2 = new JButton("2");
       butt2.setBounds(0,0,2,2);
       JButton butt3 = new JButton("3");
       butt3.setBounds(0,0,2,2);
       JButton butt4 = new JButton("4");
       butt4.setBounds(0,0,2,2);
       controlPanel.add(butt1);
       controlPanel.add(butt2);
       controlPanel.add(butt3);
       controlPanel.add(butt4);

       getContentPane().add(BorderLayout.SOUTH, controlPanel);

       butt1.addActionListener((new ActionListener() {
           public void actionPerformed(ActionEvent e) {
               butt2.setEnabled(!butt2.isEnabled());
               butt3.setEnabled(!butt3.isEnabled());
               butt4.setEnabled(!butt4.isEnabled());
           }
       } ));

       butt2.addActionListener((new ActionListener() {
           public void actionPerformed(ActionEvent e) {
           }
       } ));
    };


    @Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
            case IntersectModel.RED:
                drawRedLamp(g, x, y);
                break;
            case IntersectModel.GREEN:
                drawGreenLamp(g, x, y);
                break;
        }
    }


    public void drawAmbulance(Graphics g, int x, int y) {
        g.setColor(Color.gray);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        g.setColor(Color.pink);

        g.fillRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.drawLine(x * cellSizeW + 2, y * cellSizeH + 2, (x + 1) * cellSizeW - 2, (y + 1) * cellSizeH - 2);
        g.drawLine(x * cellSizeW + 2, (y + 1) * cellSizeH - 2, (x + 1) * cellSizeW - 2, y * cellSizeH + 2);
    }

    public void drawCar(Graphics g, int x, int y) {
        g.setColor(Color.gray);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        g.setColor(Color.pink);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.drawLine(x * cellSizeW + 2, y * cellSizeH + 2, (x + 1) * cellSizeW - 2, (y + 1) * cellSizeH - 2);
        g.drawLine(x * cellSizeW + 2, (y + 1) * cellSizeH - 2, (x + 1) * cellSizeW - 2, y * cellSizeH + 2);
    }

    public void drawRedLamp(Graphics g, int x, int y) {
        g.setColor(Color.gray);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        g.setColor(Color.red);
        g.fillRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
    }

    public void drawGreenLamp(Graphics g, int x, int y) {
        g.setColor(Color.gray);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        g.setColor(Color.green);
        g.fillRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
    }
}
