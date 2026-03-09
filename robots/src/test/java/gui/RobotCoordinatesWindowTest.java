package gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class RobotCoordinatesWindowTest {

    private RobotModel model;
    private RobotCoordinatesWindow window;

    @BeforeEach
    void setUp() {
        model = new RobotModel();
        window = new RobotCoordinatesWindow(model);
        window.setVisible(true);
    }

    @Test
    @DisplayName("Проверка создания окна")
    void testWindowCreation() {
        assertNotNull(window, "Окно должно быть создано");
        assertEquals("Координаты робота", window.getTitle(), "Заголовок окна должен быть правильным");
        assertTrue(window.isVisible(), "Окно должно быть видимым");
    }

    @Test
    @DisplayName("Проверка начальных значений через геттеры модели")
    void testInitialValues() {
        assertEquals(100.0, model.getRobotPositionX(), 0.001, "Начальный X должен быть 100");
        assertEquals(100.0, model.getRobotPositionY(), 0.001, "Начальный Y должен быть 100");
        assertEquals(0.0, model.getRobotDirection(), 0.001, "Начальное направление должно быть 0");
    }

    @Test
    @DisplayName("Проверка обновления при изменении X координаты")
    void testUpdateOnXChange() {
        double oldX = model.getRobotPositionX();

        model.setTargetPosition(200, 100);
        for (int i = 0; i < 100; i++) { // Увеличили количество итераций
            model.updateModel(10);
        }

        assertTrue(model.getRobotPositionX() > oldX,
                "X координата должна увеличиться. Было: " + oldX +
                        ", стало: " + model.getRobotPositionX());
    }

    @Test
    @DisplayName("Проверка обновления при изменении Y координаты")
    void testUpdateOnYChange() {
        double oldY = model.getRobotPositionY();

        model.setTargetPosition(100, 200);
        for (int i = 0; i < 100; i++) {
            model.updateModel(10);
        }

        assertTrue(model.getRobotPositionY() > oldY,
                "Y координата должна увеличиться. Было: " + oldY +
                        ", стало: " + model.getRobotPositionY());
    }

    @Test
    @DisplayName("Проверка обновления при изменении направления")
    void testUpdateOnDirectionChange() {
        double oldDir = model.getRobotDirection();

        model.setTargetPosition(200, 200);
        for (int i = 0; i < 100; i++) {
            model.updateModel(10);
        }

        assertTrue(model.getRobotDirection() > oldDir,
                "Направление должно увеличиться. Было: " + oldDir +
                        ", стало: " + model.getRobotDirection());
    }

    @Test
    @DisplayName("Проверка формата чисел в модели")
    void testNumberFormat() {
        model.setTargetPosition(150, 150);
        for (int i = 0; i < 100; i++) {
            model.updateModel(10);
        }

        double x = model.getRobotPositionX();
        double y = model.getRobotPositionY();
        double dir = model.getRobotDirection();

        assertFalse(Double.isNaN(x), "X не должен быть NaN");
        assertFalse(Double.isNaN(y), "Y не должен быть NaN");
        assertFalse(Double.isNaN(dir), "Направление не должно быть NaN");

        assertTrue(Double.isFinite(x), "X должен быть конечным числом");
        assertTrue(Double.isFinite(y), "Y должен быть конечным числом");
        assertTrue(Double.isFinite(dir), "Направление должно быть конечным числом");
    }

    @Test
    @DisplayName("Проверка обработки событий PropertyChange")
    void testPropertyChangeHandling() {
        final int[] callCount = {0};

        model.addPropertyChangeListener(evt -> {
            callCount[0]++;
        });

        model.setTargetPosition(200, 200);
        for (int i = 0; i < 10; i++) {
            model.updateModel(10);
        }

        assertTrue(callCount[0] > 0, "Должен быть хотя бы один вызов слушателя");
    }

    @Test
    @DisplayName("Проверка размера окна")
    void testWindowSize() {
        Dimension size = window.getSize();

        assertTrue(size.width >= 200, "Ширина окна должна быть не менее 200, сейчас: " + size.width);
        assertTrue(size.height >= 100, "Высота окна должна быть не менее 100, сейчас: " + size.height);
    }

    @Test
    @DisplayName("Проверка движения робота к цели")
    void testRobotMovement() {
        double startX = model.getRobotPositionX();
        double startY = model.getRobotPositionY();

        model.setTargetPosition(200, 200);

        for (int i = 0; i < 200; i++) {
            model.updateModel(10);
        }

        double endX = model.getRobotPositionX();
        double endY = model.getRobotPositionY();

        double startDistance = Math.sqrt(Math.pow(200 - startX, 2) + Math.pow(200 - startY, 2));
        double endDistance = Math.sqrt(Math.pow(200 - endX, 2) + Math.pow(200 - endY, 2));

        assertTrue(endDistance < startDistance,
                "Расстояние до цели должно уменьшиться. Было: " + startDistance +
                        ", стало: " + endDistance);
    }

    @Test
    @DisplayName("Проверка множественных обновлений модели")
    void testMultipleModelUpdates() {
        model.setTargetPosition(200, 200);

        assertDoesNotThrow(() -> {
            for (int i = 0; i < 1000; i++) {
                model.updateModel(10);
            }
        }, "Множественные обновления не должны вызывать ошибок");
    }

    @Test
    @DisplayName("Проверка что робот не уходит за пределы при движении")
    void testRobotStaysWithinBounds() {
        model.setTargetPosition(1000, 1000);

        for (int i = 0; i < 1000; i++) {
            model.updateModel(10);
            double x = model.getRobotPositionX();
            double y = model.getRobotPositionY();

            assertTrue(Double.isFinite(x), "X координата должна быть конечной");
            assertTrue(Double.isFinite(y), "Y координата должна быть конечной");
            assertTrue(x > -1000 && x < 2000, "X координата в разумных пределах: " + x);
            assertTrue(y > -1000 && y < 2000, "Y координата в разумных пределах: " + y);
        }
    }
}