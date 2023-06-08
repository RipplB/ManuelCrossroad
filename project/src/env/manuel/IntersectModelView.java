package manuel;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.GridWorldView;

import javax.swing.*;
import java.awt.*;


public class IntersectModelView extends GridWorldView {

    Env env;
    JButton[] buttons;

    public IntersectModelView(GridWorldModel model, String title, int windowSize,Env env) {
        super(model, title, windowSize);
        this.env = env;
        env.ambulanceExitSignal = () -> {
            for (JButton button :
                    buttons) {
                button.setEnabled(true);
            }
        };
        setVisible(true);
        repaint();
    }

   @Override
    public void initComponents(int width) {
        super.initComponents(width);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout());
        controlPanel.setSize(10,10);
        JButton butt1 = new JButton("TOP");
        butt1.setBounds(0,0,2,2);
       JButton butt2 = new JButton("RIGHT");
       butt2.setBounds(0,0,2,2);
       JButton butt3 = new JButton("BOTTOM");
       butt3.setBounds(0,0,2,2);
       JButton butt4 = new JButton("LEFT");
       butt4.setBounds(0,0,2,2);
       controlPanel.add(butt1);
       controlPanel.add(butt2);
       controlPanel.add(butt3);
       controlPanel.add(butt4);
       getContentPane().add(BorderLayout.SOUTH, controlPanel);

       buttons = new JButton[]{butt1, butt2, butt3, butt4};
       for (int i = 0; i < 4; i++) {
           int finalI = i;
           buttons[i].addActionListener(event -> {
               for (JButton button :
                       buttons) {
                   button.setEnabled(false);
               }
               env.initAmbulance(finalI);
           });
       }

    }


    @Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
            case IntersectModel.RED -> drawRedLamp(g, x, y);
            case IntersectModel.GREEN -> drawGreenLamp(g, x, y);
            case IntersectModel.AMBULANCE -> drawAmbulance(g, x, y);
            default -> drawEmpty(g, x, y);
        }
    }

    @Override
    public void drawEmpty(Graphics g, int x, int y) {
        g.setColor(new Color(238, 238, 238));
        g.fillRect(x * cellSizeW + 1, y * cellSizeH+1, cellSizeW-2, cellSizeH-2);
        g.setColor(Color.lightGray);
        g.drawRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
    }

    public void drawAmbulance(Graphics g, int x, int y) {
        g.setColor(Color.yellow);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.setColor(Color.pink);
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
