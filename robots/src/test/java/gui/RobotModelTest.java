package gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import static org.junit.jupiter.api.Assertions.*;

class RobotModelTest {

    private RobotModel model;
    private static final double DELTA = 0.001;

    @BeforeEach
    void setUp() {
        model = new RobotModel();
    }

    @Test
    @DisplayName("Проверка начальных координат")
    void testInitialPosition() {
        assertEquals(100.0, model.getRobotPositionX(), DELTA);
        assertEquals(100.0, model.getRobotPositionY(), DELTA);
        assertEquals(0.0, model.getRobotDirection(), DELTA);
    }

    @Test
    @DisplayName("Проверка установки цели")
    void testSetTargetPosition() {
        model.setTargetPosition(250, 300);
        assertEquals(250, model.getTargetPositionX());
        assertEquals(300, model.getTargetPositionY());
    }

    @Test
    @DisplayName("Робот движется к цели по горизонтали")
    void testMoveToHorizontalTarget() {
        model.setTargetPosition(200, 100); // цель справа
        double oldX = model.getRobotPositionX();

        model.updateModel(100);

        assertTrue(model.getRobotPositionX() > oldX,
                "Робот должен двигаться вправо");
        assertTrue(Math.abs(model.getRobotPositionY() - 100) < 10,
                "Y координата должна оставаться около 100");
    }

    @Test
    @DisplayName("Робот движется к цели по вертикали")
    void testMoveToVerticalTarget() {
        model.setTargetPosition(100, 200); // цель вниз
        double oldY = model.getRobotPositionY();

        model.updateModel(100);

        assertTrue(model.getRobotPositionY() > oldY,
                "Робот должен двигаться вниз");
        assertTrue(Math.abs(model.getRobotPositionX() - 100) < 10,
                "X координата должна оставаться около 100");
    }

    @Test
    @DisplayName("Робот останавливается при достижении цели")
    void testStopNearTarget() {
        model.setTargetPosition((int) 100.2, (int) 100.2);
        double oldX = model.getRobotPositionX();
        double oldY = model.getRobotPositionY();

        for (int i = 0; i < 10; i++) {
            model.updateModel(100);
        }

        assertEquals(oldX, model.getRobotPositionX(), 1.0,
                "Робот не должен далеко уйти от цели");
        assertEquals(oldY, model.getRobotPositionY(), 1.0,
                "Робот не должен далеко уйти от цели");
    }

    @Test
    @DisplayName("Проверка ограничения скорости")
    void testVelocityLimits() {
        model.setTargetPosition(1000, 100); // далекая цель
        double oldX = model.getRobotPositionX();

        model.updateModel(10); // 10 мс - стандартный тик

        // maxVelocity = 0.1 пикселей/мс, duration = 10 мс
        // Максимальное перемещение = 0.1 * 10 = 1 пиксель
        double maxPossibleDelta = 0.1 * 10; // 1.0
        double actualDelta = model.getRobotPositionX() - oldX;

        assertTrue(actualDelta <= maxPossibleDelta + 0.001,
                "Скорость не должна превышать максимум. Перемещение: " + actualDelta);
    }

    @Test
    @DisplayName("Проверка нормализации углов")
    void testAngleNormalization() {
        model.setTargetPosition(150, 100);

        for (int i = 0; i < 1000; i++) {
            model.updateModel(100);
            double direction = model.getRobotDirection();
            assertTrue(direction >= 0 && direction < 2 * Math.PI,
                    "Угол должен быть в пределах [0, 2π)");
        }
    }

    @Test
    @DisplayName("Проверка движения к цели с отрицательными координатами")
    void testMoveToNegativeCoordinates() {
        model.setTargetPosition(-50, -50);

        assertDoesNotThrow(() -> {
            for (int i = 0; i < 10; i++) {
                model.updateModel(100);
            }
        }, "Движение к отрицательным координатам не должно вызывать ошибок");
    }

    @Test
    @DisplayName("Проверка уведомлений PropertyChangeListener")
    void testPropertyChangeNotification() {
        class TestListener implements PropertyChangeListener {
            int count = 0;

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                count++;
            }
        }

        TestListener listener = new TestListener();
        model.addPropertyChangeListener(listener);

        model.updateModel(100);

        assertTrue(listener.count > 0, "Должно быть хотя бы одно уведомление");
    }

    @Test
    @DisplayName("Робот поворачивается в правильную сторону")
    void testRotationDirection() {
        // Цель справа-снизу (угол должен быть положительным)
        model.setTargetPosition(200, 200);
        model.updateModel(10);

        assertTrue(model.getRobotDirection() > 0,
                "Робот должен поворачиваться по часовой стрелке");
    }
}