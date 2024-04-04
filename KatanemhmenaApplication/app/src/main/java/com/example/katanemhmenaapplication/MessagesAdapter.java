package com.example.katanemhmenaapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;

import ergasiakatanemhmena.paketo.doulepse.Value;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {
    Context context;
    ArrayList<Value> messages;
    public MessagesAdapter(Context context, ArrayList<Value> messeges){
        this.context = context;
        this.messages = messeges;
    }

    @NonNull
    @Override
    public MessagesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //this is where you inflate the layout (giving a look to our rows)
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerviewrow_layout,parent,false);

        return new MessagesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MyViewHolder holder, int position) {
        //assing value to the views we created based on the position of the recycler view
        holder.textView.setText(messages.get(position).getPublisher());
        String[] p = ((String) messages.get(position).getValueMessage()).split(" ");
        if(p[p.length-1].equals("file")) {


            File imagefile = new File(((String) messages.get(position).getValueMessage()).split(" ")[0]);
            if (imagefile.exists()) {
                Bitmap mybitmap = BitmapFactory.decodeFile(imagefile.getAbsolutePath());
                holder.imageView.setImageBitmap(mybitmap);

                holder.imageView.setVisibility(View.VISIBLE);
            }
        }
        else{
            System.out.println(messages.isEmpty());
            String msg2="";
            for (int i=0; i<p.length-1; i++){


                msg2= msg2+" "+p[i];

            }
            System.out.println(msg2);
            holder.tvmsg.setText(msg2);
            holder.tvmsg.setVisibility(View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        //the recycler view just wants to know the number of items you wont displayed

        return messages.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        TextView tvmsg;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView2);
            tvmsg = itemView.findViewById(R.id.textView4);
        }
    }
}
