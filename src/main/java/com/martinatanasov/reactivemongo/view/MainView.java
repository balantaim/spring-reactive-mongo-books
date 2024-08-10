package com.martinatanasov.reactivemongo.view;


import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H1;
import com.martinatanasov.reactivemongo.model.BookDTO;
import com.martinatanasov.reactivemongo.service.BookService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.BigDecimalField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.selection.SingleSelect;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.CENTER;
import static com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.END;
import static org.awaitility.Awaitility.await;


@Route("vaadin")
public class MainView extends VerticalLayout {

    private final BookService bookService;
    private Grid<BookDTO> grid;

    @Autowired
    public MainView(BookService bookService) {
        this.bookService = bookService;
        setSizeFull();
        setApplicationTheme(); //Change from Light to Dark at init
        revealHeader();
        revealMain();
    }

    private void revealHeader(){
        VerticalLayout layout = new VerticalLayout();
        H1 header = new H1("Book Vault");

        Button themeSwitch = new Button("Change Theme",new Icon(VaadinIcon.MOON), click -> {
            setApplicationTheme();
        });

        layout.setWidthFull();
        layout.add(header);
        layout.add(themeSwitch);
        layout.setJustifyContentMode(CENTER);
        //Make items in the same line
        layout.setAlignItems(Alignment.CENTER);
        add(layout);
    }

    private void revealMain(){
        VerticalLayout layout = new VerticalLayout();
        HorizontalLayout buttonsLayout = new HorizontalLayout();

        grid = setGrid();

        //Init Dialog views
        Dialog addBookDialog = addDialog();
        Dialog updateDialog = updateDialog();
        Dialog alertDialog = alertDialog();
        VerticalLayout dialogLayout1 = dialogLayoutAdd(addBookDialog);
        VerticalLayout dialogLayout2 = dialogLayoutUpdate(updateDialog);
        VerticalLayout dialogLayout3 = dialogLayoutAlert(alertDialog);
        addBookDialog.add(dialogLayout1);
        updateDialog.add(dialogLayout2);
        alertDialog.add(dialogLayout3);
        //Add button
        Button addNewItem = new Button("Add", buttonClickEvent -> {
            addBookDialog.open();
        });
        //Update button
        Button edit = new Button("Edit", buttonClickEvent -> {
            updateDialog.open();
        });
        //Delete button
        Button delete = new Button("Delete", buttonClickEvent -> {
            alertDialog.open();
        });

        setGridListener(edit, delete);
        layout.add(grid);

        edit.setEnabled(false);
        delete.setEnabled(false);

        buttonsLayout.setWidthFull();
        buttonsLayout.setJustifyContentMode(END);
        buttonsLayout.setAlignItems(Alignment.STRETCH);
        buttonsLayout.add(addNewItem, edit, delete);
        add(layout, buttonsLayout, addBookDialog);
    }

    private Grid setGrid(){
        Grid<BookDTO> grid = new Grid<>(BookDTO.class, false);
        grid.addColumn(BookDTO::getId).setHeader("ID");
        grid.addColumn(BookDTO::getBookName).setHeader("Book name");
        grid.addColumn(BookDTO::getBookCategory).setHeader("Category");
        grid.addColumn(BookDTO::getBookAuthor).setHeader("Author");
        grid.addColumn(BookDTO::getPages).setHeader("pages");
        grid.addColumn(BookDTO::getBookPrice).setHeader("price");
//        grid.addColumn(BookDTO::getBookCreated).setHeader("created date");
//        grid.addColumn(BookDTO::getBookModified).setHeader("modified date");

        //Load the data
        List<BookDTO> gridData = this.bookService.getAllBooks().collectList().block();
        grid.setItems(gridData);

        return grid;
    }
    private void updateGrid(){
//        Flux<BookDTO> data = this.bookService.getAllBooks();
//        data.collectList().subscribe(grid::setItems);
            List<BookDTO> gridData = this.bookService.getAllBooks().collectList().block();
            grid.setItems(gridData);
    }

    private void setGridListener(Button edit, Button delete){
        grid.addSelectionListener(selection -> {
            Optional<BookDTO> optionalPerson = selection.getFirstSelectedItem();
            if (optionalPerson.isPresent()) {
                System.out.printf("Selected person: %s%n", optionalPerson.get().getBookName());
                edit.setEnabled(true);
                delete.setEnabled(true);
            }else{
                edit.setEnabled(false);
                delete.setEnabled(false);
            }
        });
    }

    private void addBook(TextField name, TextField category, TextField author, IntegerField pages, BigDecimalField price){
        BookDTO book = new BookDTO(null,
                name.getValue(),
                category.getValue(),
                author.getValue(),
                pages.getValue(),
                price.getValue(),
                LocalDateTime.now(),
                LocalDateTime.now());
        Mono<BookDTO> data = this.bookService.saveBook(book);
        data.subscribe(System.out::println);

        showNotification("Book added!", NotificationVariant.LUMO_SUCCESS);

        updateGrid();
    }
    private void updateBook(TextField name, TextField category, TextField author, IntegerField pages, BigDecimalField price) {
        SingleSelect<Grid<BookDTO>, BookDTO> selection = grid.asSingleSelect();
        System.out.println(selection.getValue().getId() + " was selected");

        AtomicBoolean atomicBoolean = new AtomicBoolean(false);

        BookDTO book = new BookDTO(null,
                name.getValue(),
                category.getValue(),
                author.getValue(),
                pages.getValue(),
                price.getValue(),
                null,
                LocalDateTime.now());
        Mono<BookDTO> data = this.bookService.updateBook(selection.getValue().getId(), book);
        data.subscribe(result -> {
            System.out.println(result);
            atomicBoolean.set(true);
        });

        await().untilTrue(atomicBoolean);
        updateGrid();

        showNotification("Book updated!", NotificationVariant.LUMO_SUCCESS);
    }
    private void deleteBook(){
        Set<BookDTO> item = grid.getSelectedItems();
        if(item.size() == 1){
            Optional<BookDTO> currentBook = item.stream().findFirst();
            if (currentBook.isPresent()){
                Mono<Void> result = this.bookService.deleteBookById(currentBook.get().getId());
                result.subscribe(System.out::println);

                showNotification("Book deleted!", NotificationVariant.LUMO_SUCCESS);

                updateGrid();
            }
        }
    }

    private VerticalLayout dialogLayoutAdd(Dialog dialog){
        H1 header = new H1("Add a new Book");

        TextField name = new TextField();
        name.setLabel("Book name");
        name.setWidthFull();
        TextField category = new TextField();
        category.setLabel("Category");
        category.setWidthFull();
        TextField author = new TextField();
        author.setLabel("Author");
        author.setWidthFull();

        //Pages
        IntegerField pages = new IntegerField();
        pages.setValue(1);
        pages.setStepButtonsVisible(true);
        pages.setMin(1);
        pages.setMax(5000);
        pages.setLabel("Pages");
        pages.setWidthFull();

        //Price
        BigDecimalField price = new BigDecimalField();
        price.setLabel("Price");
        price.setWidthFull();
        price.setValue(new BigDecimal("0"));
        price.setWidthFull();

        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> {
            resetFields(name, category, author, pages, price);
            dialog.close();
        });

        Button positiveButton = new Button("Add");
        positiveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        positiveButton.addClickListener(e -> {
            addBook(name, category, author, pages, price);
            resetFields(name, category, author, pages, price);
            dialog.close();
        });

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setWidthFull();
        btnLayout.setJustifyContentMode(END);
        btnLayout.add(positiveButton, closeButton);

        VerticalLayout layout = new VerticalLayout();
        layout.add(header, name, category, author, pages, price, btnLayout);
        return layout;
    }

    private VerticalLayout dialogLayoutUpdate(Dialog dialog){
        H1 header = new H1("Update Book");

        TextField name = new TextField();
        name.setLabel("Book name");
        name.setValue("");
        name.setWidthFull();
        TextField category = new TextField();
        category.setLabel("Category");
        category.setValue("");
        category.setWidthFull();
        TextField author = new TextField();
        author.setLabel("Author");
        author.setValue("");
        author.setWidthFull();

        //Pages
        IntegerField pages = new IntegerField();
        pages.setStepButtonsVisible(true);
        pages.setMin(1);
        pages.setMax(5000);
        pages.setLabel("Pages");
        pages.setValue(1);
        pages.setWidthFull();

        //Price
        BigDecimalField price = new BigDecimalField();
        price.setLabel("Price");
        price.setWidthFull();
        price.setValue(new BigDecimal("1"));
        price.setWidthFull();

        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> {
            resetFields(name, category, author, pages, price);
            dialog.close();
        });

        Button positiveButton = new Button("Update");
        positiveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        positiveButton.addClickListener(e -> {
            updateBook(name, category, author, pages, price);
            resetFields(name, category, author, pages, price);
            dialog.close();
        });

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setWidthFull();
        btnLayout.setJustifyContentMode(END);
        btnLayout.add(positiveButton, closeButton);

        VerticalLayout layout = new VerticalLayout();
        layout.add(header, name, category, author, pages, price, btnLayout);
        return layout;
    }

    private VerticalLayout dialogLayoutAlert(Dialog dialog){
        H1 header = new H1("Are you sure you want to delete this book?");

        Button closeButton = new Button("Close");
        closeButton.addClickListener(e -> dialog.close());

        Button positiveButton = new Button("Delete");
        positiveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_ERROR);
        positiveButton.addClickListener(e -> {
            deleteBook();
            dialog.close();
        });

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setWidthFull();
        btnLayout.setJustifyContentMode(END);
        btnLayout.add(positiveButton, closeButton);

        VerticalLayout layout = new VerticalLayout();
        layout.add(header, btnLayout);
        return layout;
    }
    private void resetFields(TextField name, TextField category, TextField author, IntegerField pages, BigDecimalField price){
        name.setValue("");
        category.setValue("");
        author.setValue("");
        pages.setValue(1);
        price.setValue(new BigDecimal("0"));
    }

    private Dialog alertDialog(){
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Are you sure you want to delete this book?");

        return dialog;
    }

    private Dialog addDialog(){
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Add a new Book");

        return dialog;
    }

    private Dialog updateDialog(){
        Dialog dialog = new Dialog();
        dialog.getElement().setAttribute("aria-label", "Update existing Book");

        return dialog;
    }

    private void showNotification(String message, NotificationVariant notificationVariant){
        final Notification notification = new Notification();
        notification.setText(message);
        notification.setDuration(4000);
        notification.setPosition(Notification.Position.TOP_END);
        notification.addThemeVariants(notificationVariant);
        notification.open();
    }

    private void setApplicationTheme(){
        ThemeList themeList = UI.getCurrent().getElement().getThemeList();
        if(themeList.contains(Lumo.DARK)){
            themeList.remove(Lumo.DARK);
        }else{
            themeList.add(Lumo.DARK);
        }
    }

}
