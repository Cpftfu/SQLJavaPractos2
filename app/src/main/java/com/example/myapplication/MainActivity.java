package com.example.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    private EditText titleText, descriptionText;
    private Button addButton, updateButton, deleteButton, chooseImageButton;
    private ListView listView;
    private ImageView productImageView;
    private Uri selectedImageUri;
    private ArrayAdapter<String> adapter;
    private String selectedProductTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //инициализирует библиотеку paper  для работы с хранилищем данных
        Paper.init(this);

        //привязка элементов
        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.descriptionText);
        addButton = findViewById(R.id.addButton);
        updateButton = findViewById(R.id.updateButton);
        deleteButton = findViewById(R.id.deleteButton);
        chooseImageButton = findViewById(R.id.chooseImageButton);
        listView = findViewById(R.id.ListView);
        productImageView = findViewById(R.id.productImageView);


        //создание адаптера который отображает список книг
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getProductsTitles());
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedProductTitle = adapter.getItem(position);

            Product product = Paper.book().read(selectedProductTitle, null);

            if (product != null) {
                titleText.setText(product.getTitle());
                descriptionText.setText(product.getDescription());

                productImageView.setImageURI(Uri.parse(product.getImage()));
            }
        });


        chooseImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, 1);
        });


        addButton.setOnClickListener(v -> {
            String title = titleText.getText().toString();
            String description = descriptionText.getText().toString();

            if (!title.isEmpty() && !description.isEmpty() && selectedImageUri != null) {
                Product product = new Product(title, description, selectedImageUri.toString());
                Paper.book().write(title, product);
                updateProductList();
                clearInputs();
            } else {
                Toast.makeText(MainActivity.this, "Заполните все поля и выберите изображение", Toast.LENGTH_SHORT).show();
            }
        });

        updateButton.setOnClickListener(v -> {
            if (selectedProductTitle == null) {
                Toast.makeText(MainActivity.this, "Пожавуста выберите товар!", Toast.LENGTH_SHORT).show();
                return;
            }

            String newTitle = titleText.getText().toString();
            String newDescription = descriptionText.getText().toString();

            if (!newTitle.isEmpty() && !newDescription.isEmpty() && selectedImageUri != null) {
                Paper.book().delete(selectedProductTitle);
                Product updatedProduct = new Product(newTitle, newDescription, selectedImageUri.toString());
                Paper.book().write(newTitle, updatedProduct);
                updateProductList();
                clearInputs();
            } else {
                Toast.makeText(MainActivity.this, "Заполните все поля и выберите изображение", Toast.LENGTH_SHORT).show();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (selectedProductTitle == null) {
                Toast.makeText(MainActivity.this, "Пожалуйста, выберите товар", Toast.LENGTH_SHORT).show();
                return;
            }
            Paper.book().delete(selectedProductTitle);
            updateProductList();
            clearInputs();
        });
    }

    private void updateProductList() {
        adapter.clear();
        adapter.addAll(getProductsTitles());
        adapter.notifyDataSetChanged();
    }

    private List<String> getProductsTitles() {
        return new ArrayList<>(Paper.book().getAllKeys());
    }


    private List<String> getProductTitles() {
        return new ArrayList<>(Paper.book().getAllKeys());
    }


    private void clearInputs() {
        titleText.setText("");
        descriptionText.setText("");
        selectedImageUri = null;
        productImageView.setImageURI(null);
        selectedProductTitle = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            productImageView.setImageURI(selectedImageUri); // Отображение выбранного изображения
        }
    }
}