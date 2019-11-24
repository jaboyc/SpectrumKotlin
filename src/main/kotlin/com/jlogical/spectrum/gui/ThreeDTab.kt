package com.jlogical.spectrum.gui

import com.jlogical.spectrum.model.Hue
import com.jlogical.spectrum.model.MunsellColor
import com.jlogical.spectrum.util.getColorMatrix
import com.jlogical.spectrum.util.hues
import javafx.geometry.Point3D
import javafx.scene.*
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.Sphere
import javafx.scene.transform.Affine
import javafx.scene.transform.Rotate
import tornadofx.*
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sign
import kotlin.math.sin

/**
 * All these fields are for manipulating the 3D world.
 */
var mouseStartX: Double = -1.0
var mouseStartY: Double = -1.0
var mouseX: Double = -1.0
var mouseY: Double = -1.0
var mouseOldX: Double = -1.0
var mouseOldY: Double = -1.0
var mouseDeltaX: Double = -1.0
var mouseDeltaY: Double = -1.0

/**
 * All these fields are for camera configuration.
 */
const val CAMERA_INITIAL_DISTANCE = -1450.0
const val CAMERA_NEAR_CLIP = 80.0
const val CAMERA_FAR_CLIP = 10000.0
const val MOUSE_SPEED = 0.1
const val ROTATION_SPEED = 2.0

/**
 * The radius of each sphere.
 */
const val SPHERE_RADIUS = 15.0

/**
 * Displays the Munsell Spectrum in 3D.
 */
class ThreeDTab : BorderPane() {

    /**
     * The XFormBox that holds the camera.
     */
    private val cameraXFormBox = XFormBox()

    /**
     * The camera of the scene.
     */
    private val camera = PerspectiveCamera(true)

    init {

        // Initialize scene stuff.
        val sceneRoot = Group()
        val spheres = XFormBox()
        spheres.depthTest = DepthTest.ENABLE

        // Create the light.
        val light = PointLight().apply {
            color = Color.WHITE
            translateX = 0.0
            translateY = 0.0
            translateZ = -1000.0
        }

        // Add the light to the scene.
        sceneRoot.children.add(light)

        // Add the spheres to the spheres group.
        addSpheres(spheres)

        // Add the spheres to the scene.
        sceneRoot.children.add(spheres)

        // Create a subscene.
        val scene = SubScene(sceneRoot, 500.0, 500.0, true, SceneAntialiasing.BALANCED)
        scene.widthProperty().bind(widthProperty())
        scene.heightProperty().bind(heightProperty())
        scene.fill = Color.BLACK

        // Interact with mouse presses.
        scene.setOnMousePressed {
            mouseStartX = it.sceneX
            mouseStartY = it.sceneY
            mouseX = it.sceneX
            mouseY = it.sceneY
            mouseOldX = it.sceneX
            mouseOldY = it.sceneY
        }

        // When mouse is dragged, either rotate the camera or pan it around.
        scene.setOnMouseDragged {
            mouseOldX = mouseX
            mouseOldY = mouseY
            mouseX = it.sceneX
            mouseY = it.sceneY
            mouseDeltaX = (mouseX - mouseOldX)
            mouseDeltaY = (mouseY - mouseOldY)

            if (it.isPrimaryButtonDown) {
                spheres.addRotation(-mouseDeltaX * MOUSE_SPEED * ROTATION_SPEED, Rotate.Y_AXIS)
                spheres.addRotation(mouseDeltaY * MOUSE_SPEED * ROTATION_SPEED, Rotate.X_AXIS)
            } else if (it.isSecondaryButtonDown) {
                spheres.translateX += mouseDeltaX
                spheres.translateY += mouseDeltaY
            }
        }

        // Zoom on scroll.
        scene.setOnScroll {
            camera.translateZ += it.deltaY
        }

        // Initialize the scene's camera.
        sceneRoot.children.add(camera)
        cameraXFormBox.add(camera)
        camera.nearClip = CAMERA_NEAR_CLIP
        camera.farClip = CAMERA_FAR_CLIP
        camera.translateZ = CAMERA_INITIAL_DISTANCE
        camera.translateY = 180.0
        cameraXFormBox.addRotation(-10.0, Rotate.Z_AXIS)
        camera.depthTest = DepthTest.ENABLE

        // Set the camera.
        scene.camera = camera

        // Add the subscene.
        center = scene
    }
}

/**
 * Adds all the spheres to the scene root.
 */
private fun addSpheres(sceneRoot: Group) {
    // Counter for rotation.
    var counter = 0

    // Add each color slice to the root.
    hues.forEach {
        addColorSlice(it, PI * 2 / hues.size * counter++, sceneRoot)
    }
}

/**
 * Adds the given [hue]'s color slice to the [sceneRoot] with the given [rotation] about the Y-Axis.
 */
private fun addColorSlice(hue: Hue, rotation: Double, sceneRoot: Group) {

    // The color slice of the matrix.
    val slice = getColorMatrix(hue)

    // Add each color of the hue to the root.
    for (i in 0 until slice.size) {
        for (j in 0 until slice[i].size) {
            sceneRoot.children += createColorSphere(slice[i][j], j * 45.0, i * 45.0, rotation)
        }
    }
}

/**
 * Creates a color sphere with the given [color], [x] and [y], and [rotation] about the Y-Axis.
 */
private fun createColorSphere(color: MunsellColor, x: Double, y: Double, rotation: Double): Sphere {
    val rgb = color.color // The RGB of the munsell.

    val mat = PhongMaterial(rgb) // The material of the sphere.

    // Create and return the sphere.
    return Sphere(SPHERE_RADIUS).apply {
        translateX = cos(rotation) * x + cos(rotation).sign
        translateY = y + SPHERE_RADIUS
        translateZ = sin(rotation) * x + sin(rotation).sign
        material = mat

        setOnMouseClicked {
            if(it.clickCount == 2) DetailsTab.showColorDetails(color)
        }
    }

}

/**
 * A container that contains some rotational helper methods.
 * Adapted from https://stackoverflow.com/questions/46176489/javafx-3d-rotation-around-scene-fixed-axes
 */
class XFormBox : Group() {
    init {
        transforms.add(Affine())
    }

    /**
     * Accumulate rotation about specified axis.
     */
    fun addRotation(angle: Double, axis: Point3D) {
        val r = Rotate(angle, axis)

        /**
         * This is the important bit and thanks to bronkowitz in this post
         * https://stackoverflow.com/questions/31382634/javafx-3d-rotations for getting
         * me to the solution that the rotations need accumulated in this way
         */
        transforms[0] = r.createConcatenation(transforms[0])
    }

    /**
     * Reset transform to identity transform
     */
    fun reset() {
        transforms[0] = Affine()
    }
}