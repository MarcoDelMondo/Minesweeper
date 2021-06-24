import java.util.*;
import java.io.*;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class Minesweeper extends Application {
	private static boolean flag = false;
	private static boolean firstClick = true;
	private static boolean gameIsOver = false;
	private static int[][] mines;
	private static boolean[][] isFlagged;
	private static boolean[][] isClicked;
	private static boolean[][] isRevealed;
	private static Button[][] buttons;
	private static int sizeX;
	private static int sizeY;
	private static int spaces = 0;
	private static int bombsTotal = 0;
	private static int bombsLeft = 0;
	private static boolean end = false;
	private static String difficulty = "i";
	public static int counter = 0;
	private static int elapsedSeconds = 0;

	public static void main(String[] args) {
		resetGame();
		launch(args);
	}

	public void start(Stage theStage) {
		try {
			FileInputStream input = new FileInputStream("res/face-smile.png");
			Image image = new Image(input);
			ImageView imageView = new ImageView(image);
			HBox hBox2 = new HBox(10);
			hBox2.setPrefWidth(50);
			Button face = new Button("", imageView);
			face.setMinSize(50, 50);
			face.setMaxSize(50, 50);
			imageView.setFitHeight(0);
			imageView.setFitWidth(0);
			hBox2.getChildren().add(face);
			input.close();
			BorderPane bp = new BorderPane();
			BorderPane bp2 = new BorderPane();
			BorderPane bp3 = new BorderPane();
			bp.setPadding(Insets.EMPTY);
			HBox hBox = new HBox(10);
			hBox.setPrefWidth(50);
			Label time = new Label("000");
			Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
				elapsedSeconds++;
				time.setText(elapsedSeconds + "");
			}));
			timer.setCycleCount(Animation.INDEFINITE);
			hBox.getChildren().add(time);
			time.setFont(Font.font("Arial", FontWeight.BOLD, 20));
			HBox hBox3 = new HBox(10);
			hBox3.setPrefWidth(50);
			Label bombLabel = new Label("0" + bombsLeft);
			bombLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
			hBox3.getChildren().add(bombLabel);
			GridPane top = new GridPane();
			top.add(bp2, 0, 0);
			top.add(bp3, 2, 0);
			bp2.setCenter(hBox);
			bp3.setCenter(hBox3);
			bp.setTop(top);
			top.setMinSize(100, 30);
			top.setMaxSize(1000, 100);
			top.add(hBox3, 0, 0);
			top.add(hBox2, 5, 0);
			top.add(hBox, 10, 0);
			top.setPadding(new Insets(12, 30, 12, 150));
			hBox.setAlignment(Pos.CENTER);
			hBox2.setAlignment(Pos.CENTER);
			hBox3.setAlignment(Pos.CENTER);
			GridPane gp = new GridPane();
			bp.setCenter(gp);
			for (int i = 0; i < sizeX; i++) {
				for (int j = 0; j < sizeY; j++) {
					final int a = i;
					final int b = j;
					ImageView image2 = new ImageView(new Image(new FileInputStream("res/cover.png")));
					Button button = new Button("", image2);
					button.setMinSize(30, 30);
					button.setMaxSize(30, 30);
					image2.setFitHeight(0);
					image2.setFitWidth(0);
					image2.fitWidthProperty().add(button.widthProperty());
					image2.fitHeightProperty().add(button.widthProperty());
					buttons[j][i] = button;
					button.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							timer.play();
							if (e.getButton() == MouseButton.SECONDARY) {
								if (isRevealed[b][a] == false) {
									if (isFlagged[b][a] == false)
										isFlagged[b][a] = true;
									else
										isFlagged[b][a] = false;
									if (isFlagged[b][a] == true) {
										if (bombsLeft > 0) {
											try {
												ImageView image = new ImageView(
														new Image(new FileInputStream("res/flag.png")));
												buttons[b][a].setMaxHeight(0);
												buttons[b][a].setMaxWidth(0);
												image.fitWidthProperty().add(buttons[b][a].widthProperty());
												image.fitHeightProperty().add(buttons[b][a].heightProperty());
												buttons[b][a].setGraphic(image);
												bombsLeft--;
												String bombString = ("0" + bombsLeft);
												Label bombLabel = new Label(bombString);
												bombLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
												hBox3.getChildren().clear();
												hBox3.getChildren().add(bombLabel);
											} catch (IOException g) {
											}
										} else {
											isFlagged[b][a] = false;
										}
									} else if (isFlagged[b][a] == false) {
										try {
											ImageView image = new ImageView(
													new Image(new FileInputStream("res/cover.png")));
											buttons[b][a].setMaxHeight(0);
											buttons[b][a].setMaxWidth(0);
											image.fitWidthProperty().add(buttons[b][a].widthProperty());
											image.fitHeightProperty().add(buttons[b][a].heightProperty());
											buttons[b][a].setGraphic(image);
											bombsLeft++;
											String bombString = ("0" + bombsLeft);
											Label bombLabel = new Label(bombString);
											hBox3.getChildren().clear();
											hBox3.getChildren().add(bombLabel);
										} catch (IOException g) {
										}
									}

								}
							}
							if (e.getButton() == MouseButton.PRIMARY) {
								if (isFlagged[b][a] == false) {
									if (mines[b][a] == 0) {
										openTiles(buttons[b][a], b, a, 0);
									}
									if (mines[b][a] == -1) {
										gameOver(face, buttons[b][a]);
									} else if (isClicked[b][a] == true) {
										checkFlags(b, a, buttons[b][a], face);
									} else if (mines[b][a] >= 0) {
										showNumber(buttons[b][a], b, a);
									}
									if (counter == sizeX * sizeY - bombsTotal)
										won(face);
								}
							}
							e.consume();
						}
					});
					gp.add(buttons[j][i], i, j);
				}
			}
			face.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					timer.stop();
					elapsedSeconds = 0;
					resetGame();
					start(theStage);
				}
			});
			theStage.setScene(new Scene(bp, sizeX * 30, (sizeY * 30) + 50));
			timer.stop();
		} catch (IOException e) {
		}
		theStage.show();
	}

	public static void showNumber(Button b, int c, int a) {
		if (flag == false) {
			isRevealed[c][a] = true;
		}
		flag = false;
		if (mines[c][a] == 0) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/0.png")));
				b.setMaxHeight(0);
				b.setMaxWidth(0);
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 2) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/2.png")));
				b.setMaxHeight(0);
				b.setMaxWidth(0);
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 1) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/1.png")));
				b.setMaxHeight(0);
				b.setMaxWidth(0);
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 3) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/3.png")));
				b.setMaxHeight(0);
				b.setMaxWidth(0);
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 4) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/4.png")));
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 5) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/5.png")));
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 6) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/6.png")));
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 7) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/7.png")));
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 8) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/8.png")));
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		}

	}

	public static void showNumber(Button b, int c, int a, int k) {
		if (flag == false) {
			isRevealed[c][a] = true;
		}
		flag = false;
		if (mines[c][a] == 0) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/0.png")));
				b.setMaxHeight(0);
				b.setMaxWidth(0);
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 2) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/2.png")));
				b.setMaxHeight(0);
				b.setMaxWidth(0);
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 1) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/1.png")));
				b.setMaxHeight(0);
				b.setMaxWidth(0);
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 3) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/3.png")));
				b.setMaxHeight(0);
				b.setMaxWidth(0);
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 4) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/4.png")));
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 5) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/5.png")));
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 6) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/6.png")));
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 7) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/7.png")));
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		} else if (mines[c][a] == 8) {
			try {
				ImageView image = new ImageView(new Image(new FileInputStream("res/8.png")));
				image.fitWidthProperty().add(b.widthProperty());
				image.fitHeightProperty().add(b.heightProperty());
				b.setGraphic(image);
				if (isClicked[c][a] == false) {
					counter++;
					isClicked[c][a] = true;
				}
			} catch (IOException e) {
			}
		}

		if (k > 0)
			openTiles(buttons[c][a], c, a, k);

	}

	public static void gameOver(Button b, Button b2) {
		gameIsOver = true;
		try {
			b.setGraphic(new ImageView(new Image(new FileInputStream("res/face-dead.png"))));
			for (int i = 0; i < sizeX; i++) {
				for (int j = 0; j < sizeY; j++) {
					if (mines[j][i] == -1) {
						Button bu = buttons[j][i];
						bu.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-grey.png"))));
					}
				}
			}
			b2.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-red.png"))));
			for (int i = 0; i < sizeX; i++) {
				for (int j = 0; j < sizeY; j++) {
					buttons[j][i].setDisable(true);
				}
			}
		} catch (IOException e) {
		}
	}

	public static void gameOver(Button b, Button[][][] b2, Button[][][] b3) {
		gameIsOver = true;
		try {
			b.setGraphic(new ImageView(new Image(new FileInputStream("res/face-dead.png"))));

			for (int i = 0; i < sizeX; i++) {
				for (int j = 0; j < sizeY; j++) {
					if (isFlagged[j][i] == true && mines[j][i] != -1) {
						Button bu = buttons[j][i];
						bu.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-misflagged.png"))));
					}
					if (mines[j][i] == -1) {
						Button bu = buttons[j][i];
						bu.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-grey.png"))));
					}
				}
			}
			for (int i = 0; i < sizeX; i++) {
				for (int j = 0; j < sizeY; j++) {
					buttons[j][i].setDisable(true);
				}
			}

		} catch (IOException e) {
		}
	}

	public static void won(Button b) {
		gameIsOver = true;
		try {
			b.setGraphic(new ImageView(new Image(new FileInputStream("res/face-win.png"))));
		} catch (IOException e) {
		}
	}

	public static void checkFlags(int b, int a, Button button, Button resetButton) {
		boolean endGame = false;
		Button[][][] changeButtons = new Button[8][1][1];
		Button[][][] redButtons = new Button[8][1][1];
		int u = 0;
		int p = 0;
		int tilesToGet = 0;
		int[][][] field = new int[8][2][1];
		int valueOfFlag = mines[b][a];
		int counter = 0;
		boolean topLeft = true;
		boolean topMid = true;
		boolean topRight = true;
		boolean midLeft = true;
		boolean midRight = true;
		boolean botMid = true;
		boolean botRight = true;
		boolean botLeft = true;
		if (b == 0) {
			botLeft = false;
			topLeft = false;
			midLeft = false;
		}
		if (b == mines.length - 1) {
			botRight = false;
			topRight = false;
			midRight = false;
		}
		if (a == 0) {
			topMid = false;
			topLeft = false;
			topRight = false;
		}
		if (a == mines[0].length - 1) {
			botLeft = false;
			botMid = false;
			botRight = false;
		}
		if (topLeft)
			if (isFlagged[b - 1][a - 1] == true)
				u++;
		if (topMid)
			if (isFlagged[b][a - 1] == true)
				u++;

		if (topRight)
			if (isFlagged[b + 1][a - 1] == true)
				u++;
		if (midLeft)
			if (isFlagged[b - 1][a] == true)
				u++;
		if (midRight)
			if (isFlagged[b + 1][a] == true)
				u++;
		if (botLeft)
			if (isFlagged[b - 1][a + 1] == true)
				u++;
		if (botRight)
			if (isFlagged[b + 1][a + 1] == true)
				u++;
		if (botMid)
			if (isFlagged[b][a + 1] == true)
				u++;

		if (topLeft) {
			tilesToGet++;
			if (mines[b - 1][a - 1] != -1 && isFlagged[b - 1][a - 1] == true) {
				if (u == valueOfFlag) {
					try {
						buttons[b - 1][a - 1]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-misflagged.png"))));
					} catch (FileNotFoundException e) {
					}
				}
				endGame = true;
			}
			if (mines[b - 1][a - 1] == -1 && isFlagged[b - 1][a - 1] == false) {
				if (u == valueOfFlag) {
					try {
						buttons[b - 1][a - 1]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-red.png"))));
					} catch (FileNotFoundException e) {
					}
				}
			}
			if (mines[b - 1][a - 1] == -1 && isFlagged[b - 1][a - 1] == true) {

			} else {
				field[counter][0][0] = (b - 1);
				field[counter][1][0] = (a - 1);
				counter++;
			}

		}
		if (topMid) {
			tilesToGet++;
			if (mines[b][a - 1] != -1 && isFlagged[b][a - 1] == true) {
				if (u == valueOfFlag) {
					try {
						buttons[b][a - 1]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-misflagged.png"))));
					} catch (FileNotFoundException e) {
					}
				}
				endGame = true;
			}
			if (mines[b][a - 1] == -1 && isFlagged[b][a - 1] == false) {
				if (u == valueOfFlag) {
					try {
						buttons[b][a - 1].setGraphic(new ImageView(new Image(new FileInputStream("res/mine-red.png"))));
					} catch (FileNotFoundException e) {
					}
				}
			}
			if (mines[b][a - 1] == -1 && isFlagged[b][a - 1] == true) {

			} else {
				field[counter][0][0] = (b);
				field[counter][1][0] = (a - 1);
				counter++;
			}
		}
		if (topRight) {
			tilesToGet++;
			if (mines[b + 1][a - 1] != -1 && isFlagged[b + 1][a - 1] == true) {
				if (u == valueOfFlag) {
					try {
						buttons[b + 1][a - 1]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-misflagged.png"))));
					} catch (FileNotFoundException e) {
					}
				}
				endGame = true;
			}
			if (mines[b + 1][a - 1] == -1 && isFlagged[b + 1][a - 1] == false) {
				if (u == valueOfFlag) {
					try {
						buttons[b + 1][a - 1]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-red.png"))));
					} catch (FileNotFoundException e) {
					}
				}
			}
			if (mines[b + 1][a - 1] == -1 && isFlagged[b + 1][a - 1] == true) {

			} else {
				field[counter][0][0] = (b + 1);
				field[counter][1][0] = (a - 1);
				counter++;
			}
		}
		if (midLeft) {
			tilesToGet++;
			if (mines[b - 1][a] != -1 && isFlagged[b - 1][a] == true) {
				if (u == valueOfFlag) {
					try {
						buttons[b - 1][a]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-misflagged.png"))));
					} catch (FileNotFoundException e) {
					}
				}
				endGame = true;
			}
			if (mines[b - 1][a] == -1 && isFlagged[b - 1][a] == false) {
				if (u == valueOfFlag) {
					try {
						buttons[b - 1][a]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-misflagged.png"))));
					} catch (FileNotFoundException e) {
					}
				}
				endGame = true;
			}
			if (mines[b - 1][a] == -1 && isFlagged[b - 1][a] == true) {

			} else {
				field[counter][0][0] = (b - 1);
				field[counter][1][0] = (a);
				counter++;
			}
		}
		if (midRight) {
			tilesToGet++;
			if (mines[b + 1][a] != -1 && isFlagged[b + 1][a] == true) {
				if (u == valueOfFlag) {
					try {
						buttons[b + 1][a]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-misflagged.png"))));
					} catch (FileNotFoundException e) {
					}
				}
				endGame = true;
			}
			if (mines[b + 1][a] == -1 && isFlagged[b + 1][a] == false) {
				if (u == valueOfFlag) {
					try {
						buttons[b + 1][a].setGraphic(new ImageView(new Image(new FileInputStream("res/mine-red.png"))));
					} catch (FileNotFoundException e) {
					}
				}
			}
			if (mines[b + 1][a] == -1 && isFlagged[b + 1][a] == true) {
			} else {
				field[counter][0][0] = (b + 1);
				field[counter][1][0] = (a);
				counter++;
			}
		}
		if (botLeft) {

			tilesToGet++;
			if (mines[b - 1][a + 1] != -1 && isFlagged[b - 1][a + 1] == true) {
				if (u == valueOfFlag) {
					try {
						buttons[b - 1][a + 1]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-misflagged.png"))));
					} catch (FileNotFoundException e) {
					}
				}
				endGame = true;
			}
			if (mines[b - 1][a + 1] == -1 && isFlagged[b - 1][a + 1] == false) {
				if (u == valueOfFlag) {
					try {
						buttons[b - 1][a + 1]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-red.png"))));
					} catch (FileNotFoundException e) {
					}
				}
			}
			if (mines[b - 1][a + 1] == -1 && isFlagged[b - 1][a + 1] == true) {

			} else {
				field[counter][0][0] = (b - 1);
				field[counter][1][0] = (a + 1);
				counter++;
			}
		}
		if (botRight) {
			tilesToGet++;
			if (mines[b + 1][a + 1] != -1 && isFlagged[b + 1][a + 1] == true) {
				if (u == valueOfFlag) {
					try {
						buttons[b + 1][a + 1]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-misflagged.png"))));
					} catch (FileNotFoundException e) {
					}
				}
				endGame = true;
			}
			if (mines[b + 1][a + 1] == -1 && isFlagged[b + 1][a + 1] == false) {
				if (u == valueOfFlag) {
					try {
						buttons[b + 1][a + 1]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-red.png"))));
					} catch (FileNotFoundException e) {
					}
				}
			} else {
				field[counter][0][0] = (b + 1);
				field[counter][1][0] = (a + 1);
				counter++;
			}
		}
		if (botMid) {
			tilesToGet++;
			if (mines[b][a + 1] != -1 && isFlagged[b][a + 1] == true) {
				if (u == valueOfFlag) {
					try {
						buttons[b][a + 1]
								.setGraphic(new ImageView(new Image(new FileInputStream("res/mine-misflagged.png"))));
					} catch (FileNotFoundException e) {
					}
				}
				endGame = true;
			}
			if (mines[b][a + 1] == -1 && isFlagged[b][a + 1] == false) {
				if (u == valueOfFlag) {
					try {
						buttons[b][a + 1].setGraphic(new ImageView(new Image(new FileInputStream("res/mine-red.png"))));
					} catch (FileNotFoundException e) {
					}
				}
			} else {
				field[counter][0][0] = (b);
				field[counter][1][0] = (a + 1);
				counter++;
			}
		}
		if (endGame && u == valueOfFlag) {
			gameOver(resetButton, changeButtons, redButtons);
		}
		if ((tilesToGet - counter) == valueOfFlag) {

			endGame = false;
			for (int l = 0; l < counter; l++) {
				if (isFlagged[field[l][0][0]][field[l][1][0]] == false) {
					showNumber(buttons[field[l][0][0]][field[l][1][0]], field[l][0][0], field[l][1][0]);
				}
			}

		}
		counter = 0;
		topLeft = true;
		topMid = true;
		topRight = true;
		midLeft = true;
		midRight = true;
		botMid = true;
		botRight = true;
		botLeft = true;
	}

	public static void openTiles(Button b, int c, int a, int count) {
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {

				boolean topLeft = true;
				boolean topMid = true;
				boolean topRight = true;
				boolean midLeft = true;
				boolean midRight = true;
				boolean botMid = true;
				boolean botRight = true;
				boolean botLeft = true;
				if (c == 0) {
					botLeft = false;
					topLeft = false;
					midLeft = false;
				}
				if (c == mines.length - 1) {
					botRight = false;
					topRight = false;
					midRight = false;
				}
				if (a == 0) {
					topMid = false;
					topLeft = false;
					topRight = false;
				}
				if (a == mines[0].length - 1) {
					botLeft = false;
					botMid = false;
					botRight = false;
				}
				if (topLeft)
					if (isClicked[c - 1][a - 1] == false && mines[c][a] == 0) {
						showNumber(buttons[c - 1][a - 1], c - 1, a - 1, count + 1);
					}
				if (topMid)
					if (isClicked[c][a - 1] == false && mines[c][a] == 0) {
						showNumber(buttons[c][a - 1], c, a - 1, count + 1);
					}
				if (topRight)
					if (isClicked[c + 1][a - 1] == false && mines[c][a] == 0) {
						showNumber(buttons[c + 1][a - 1], c + 1, a - 1, count + 1);
					}
				if (midLeft)
					if (isClicked[c - 1][a] == false && mines[c][a] == 0) {
						showNumber(buttons[c - 1][a], c - 1, a, count + 1);
					}
				if (midRight)
					if (isClicked[c + 1][a] == false && mines[c][a] == 0) {
						showNumber(buttons[c + 1][a], c + 1, a, count + 1);
					}
				if (botLeft)
					if (isClicked[c - 1][a + 1] == false && mines[c][a] == 0) {
						showNumber(buttons[c - 1][a + 1], c - 1, a + 1, count + 1);
					}
				if (botRight)
					if (isClicked[c + 1][a + 1] == false && mines[c][a] == 0) {
						showNumber(buttons[c + 1][a + 1], c + 1, a + 1, count + 1);
					}
				if (botMid)
					if (isClicked[c][a + 1] == false && mines[c][a] == 0) {
						showNumber(buttons[c][a + 1], c, a + 1, count + 1);
					}
			}
		}

	}

	public static void bombPosition() {
		int i = 0;
		int[][] places = new int[bombsTotal * 2][2];
		while (i < bombsTotal) {
			int random = (int) (Math.random() * (sizeX));
			int random2 = (int) (Math.random() * (sizeY));
			mines[random2][random] = -1;
			places[i][0] = random;
			places[i][1] = random2;
			try {
				for (int m = 0; m < i; m++) {
					while (places[m][0] == random && places[m][1] == random2) {
						if (places[m][0] == random && places[m][1] == random2) {
							m = 0;
						}
						random = (int) (Math.random() * (sizeX));
						random2 = (int) (Math.random() * (sizeY));
						mines[random2][random] = -1;
					}
				}
			} catch (IndexOutOfBoundsException e) {
			}
			i++;
		}
		setNumbers();
	}

	public static void setNumbers() {
		boolean topLeft = true;
		boolean topMid = true;
		boolean topRight = true;
		boolean midLeft = true;
		boolean midRight = true;
		boolean botMid = true;
		boolean botRight = true;
		boolean botLeft = true;
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				if (mines[j][i] == -1) {
					if (j == 0) {
						botLeft = false;
						topLeft = false;
						midLeft = false;
					}
					if (j == mines.length - 1) {
						botRight = false;
						topRight = false;
						midRight = false;
					}
					if (i == 0) {
						topMid = false;
						topLeft = false;
						topRight = false;
					}
					if (i == mines[0].length - 1) {
						botLeft = false;
						botMid = false;
						botRight = false;
					}
					if (topLeft) {
						if (mines[j - 1][i - 1] != -1)
							mines[j - 1][i - 1]++;
					}
					if (topMid) {
						if (mines[j][i - 1] != -1)
							mines[j][i - 1]++;
					}
					if (topRight) {
						if (mines[j + 1][i - 1] != -1)
							mines[j + 1][i - 1]++;
					}
					if (midLeft) {
						if (mines[j - 1][i] != -1)
							mines[j - 1][i]++;
					}
					if (midRight) {
						if (mines[j + 1][i] != -1)
							mines[j + 1][i]++;
					}
					if (botLeft) {
						if (mines[j - 1][i + 1] != -1)
							mines[j - 1][i + 1]++;
					}
					if (botRight) {
						if (mines[j + 1][i + 1] != -1)
							mines[j + 1][i + 1]++;
					}
					if (botMid) {
						if (mines[j][i + 1] != -1)
							mines[j][i + 1]++;
					}
					topLeft = true;
					topMid = true;
					topRight = true;
					midLeft = true;
					midRight = true;
					botMid = true;
					botRight = true;
					botLeft = true;
				}
			}
		}
	}

	public static void resetGame() {
		firstClick = true;
		spaces = 0;
		bombsTotal = 0;
		bombsLeft = 0;
		end = false;
		difficulty = "i";
		counter = 0;
		if (difficulty.equals("b")) {
			sizeX = 8;
			sizeY = 8;
			bombsTotal = 10;
		} else if (difficulty.equals("i")) {
			sizeX = 16;
			sizeY = 16;
			bombsTotal = 40;
		} else if (difficulty.equals("e")) {
			sizeX = 32;
			sizeY = 16;
			bombsTotal = 99;
		}
		mines = new int[sizeY][sizeX];
		isFlagged = new boolean[sizeY][sizeX];
		isClicked = new boolean[sizeY][sizeX];
		isRevealed = new boolean[sizeY][sizeX];
		buttons = new Button[sizeY][sizeX];
		for (int i = 0; i < sizeX; i++) {
			for (int j = 0; j < sizeY; j++) {
				mines[j][i] = 0;
				isFlagged[j][i] = false;
				isClicked[j][i] = false;
				isRevealed[j][i] = false;
				buttons[j][i] = null;
			}
		}
		bombsLeft = bombsTotal;
		bombPosition();
	}
}