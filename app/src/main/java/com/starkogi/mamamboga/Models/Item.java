package com.starkogi.mamamboga.Models;

import android.os.Parcel;
import android.os.Parcelable;



public class Item implements Parcelable {
    private String itemName;
    private String itemDescription;
    private String itemUid;
    private int itemPrice;
    private int itemCount;
    private String image;

    public Item() {}  // Needed for Firebase

    public Item(String itemName, String itemDescription, String itemUid, int itemPrice, int itemCount, String image) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.itemUid = itemUid;
        this.itemPrice = itemPrice;
        this.itemCount = itemCount;
        this.image = image;
    }

    protected Item(Parcel in) {
        itemName = in.readString();
        itemDescription = in.readString();
        itemUid = in.readString();
        itemPrice = in.readInt();
        itemCount = in.readInt();
        image = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>() {
        @Override
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        @Override
        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getItemUid() {
        return itemUid;
    }

    public void setItemUid(String itemUid) {
        this.itemUid = itemUid;
    }

    public int getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(int itemPrice) {
        this.itemPrice = itemPrice;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(itemDescription);
        dest.writeString(itemUid);
        dest.writeInt(itemPrice);
        dest.writeInt(itemCount);
        dest.writeString(image);
    }
}
