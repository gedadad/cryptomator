package org.cryptomator.ui.migration;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.cryptomator.ui.common.DefaultSceneFactory;
import org.cryptomator.ui.common.FXMLLoaderFactory;
import org.cryptomator.ui.common.FxController;
import org.cryptomator.ui.common.FxControllerKey;
import org.cryptomator.ui.common.FxmlFile;
import org.cryptomator.ui.common.FxmlScene;
import org.cryptomator.ui.common.StackTraceController;
import org.cryptomator.ui.mainwindow.MainWindow;

import javax.inject.Named;
import javax.inject.Provider;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

@Module
abstract class MigrationModule {

	@Provides
	@MigrationWindow
	@MigrationScoped
	static FXMLLoaderFactory provideFxmlLoaderFactory(Map<Class<? extends FxController>, Provider<FxController>> factories, DefaultSceneFactory sceneFactory, ResourceBundle resourceBundle) {
		return new FXMLLoaderFactory(factories, sceneFactory, resourceBundle);
	}

	@Provides
	@MigrationWindow
	@MigrationScoped
	static Stage provideStage(@MainWindow Stage owner, ResourceBundle resourceBundle, @Named("windowIcons") List<Image> windowIcons) {
		Stage stage = new Stage();
		stage.setTitle(resourceBundle.getString("migration.title"));
		stage.setResizable(false);
		stage.initModality(Modality.WINDOW_MODAL);
		stage.initOwner(owner);
		stage.getIcons().addAll(windowIcons);
		return stage;
	}

	@Provides
	@Named("genericErrorCause")
	@MigrationScoped
	static ObjectProperty<Throwable> provideGenericErrorCause() {
		return new SimpleObjectProperty<>();
	}

	@Provides
	@FxmlScene(FxmlFile.MIGRATION_START)
	@MigrationScoped
	static Scene provideMigrationStartScene(@MigrationWindow FXMLLoaderFactory fxmlLoaders) {
		return fxmlLoaders.createScene("/fxml/migration_start.fxml");
	}

	@Provides
	@FxmlScene(FxmlFile.MIGRATION_RUN)
	@MigrationScoped
	static Scene provideMigrationRunScene(@MigrationWindow FXMLLoaderFactory fxmlLoaders) {
		return fxmlLoaders.createScene("/fxml/migration_run.fxml");
	}

	@Provides
	@FxmlScene(FxmlFile.MIGRATION_SUCCESS)
	@MigrationScoped
	static Scene provideMigrationSuccessScene(@MigrationWindow FXMLLoaderFactory fxmlLoaders) {
		return fxmlLoaders.createScene("/fxml/migration_success.fxml");
	}

	@Provides
	@FxmlScene(FxmlFile.MIGRATION_GENERIC_ERROR)
	@MigrationScoped
	static Scene provideMigrationGenericErrorScene(@MigrationWindow FXMLLoaderFactory fxmlLoaders) {
		return fxmlLoaders.createScene("/fxml/migration_generic_error.fxml");
	}

	// ------------------

	@Binds
	@IntoMap
	@FxControllerKey(MigrationStartController.class)
	abstract FxController bindMigrationStartController(MigrationStartController controller);

	@Binds
	@IntoMap
	@FxControllerKey(MigrationRunController.class)
	abstract FxController bindMigrationRunController(MigrationRunController controller);

	@Binds
	@IntoMap
	@FxControllerKey(MigrationSuccessController.class)
	abstract FxController bindMigrationSuccessController(MigrationSuccessController controller);

	@Binds
	@IntoMap
	@FxControllerKey(MigrationGenericErrorController.class)
	abstract FxController bindMigrationGenericErrorController(MigrationGenericErrorController controller);

	@Provides
	@IntoMap
	@FxControllerKey(StackTraceController.class)
	static FxController provideStackTraceController(@Named("genericErrorCause") ObjectProperty<Throwable> errorCause) {
		return new StackTraceController(errorCause.get());
	}

}
