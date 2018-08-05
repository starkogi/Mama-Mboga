package com.starkogi.mamamboga.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.starkogi.mamamboga.Cart;
import com.starkogi.mamamboga.MainActivity;
import com.starkogi.mamamboga.Models.Item;
import com.starkogi.mamamboga.R;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Items.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Items#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Items extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public Items() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Items.
     */
    // TODO: Rename and change types and number of parameters
    public static Items newInstance(String param1, String param2) {
        Items fragment = new Items();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private RecyclerView rv_items;
    FirebaseRecyclerAdapter adapter;

    private ItemsAdaptor adaptor;
    private View main_view;
    private RecyclerView.LayoutManager mLayoutManager;

    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference("items/");

    ArrayList<Item> items = new ArrayList<>();
    public ArrayList<Item> selectedItems = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        main_view = inflater.inflate(R.layout.fragment_items, container, false);

        initComponents();
        return main_view;
    }

    private void initComponents() {
        rv_items = main_view.findViewById(R.id.rv_items);

// use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        rv_items.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new GridLayoutManager(getContext(), 2);
// set a StaggeredGridLayoutManager with 3 number of columns and vertical orientation
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                LinearLayoutManager.VERTICAL);
        rv_items.setLayoutManager(staggeredGridLayoutManager); // set LayoutManager to RecyclerView

        adaptor = new ItemsAdaptor(items);
        rv_items.setAdapter(adaptor);

        getDatabaseItems();

        ((MainActivity)getActivity()).fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getContext(), Cart.class);

                mIntent.putParcelableArrayListExtra ("extra", selectedItems);

                startActivity(mIntent);

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void addDatabaseItem(){

        String uId = ref.push().getKey();

        ref.child(uId).setValue(new Item("Cabbage", "Vegitables Fresh from Shamba", uId, 10, 100, "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/2wCEAAkGBxMQEBASEBIPFRUWFRUQFhAQEhAPEBUVFRUWFxUVFRUYHSggGBolGxUXITEhJSkrLi4uFx8zODMvNygtLisBCgoKDg0OGxAQGy0lICUtLS0tLS0tLSstLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLS0tLf/AABEIALcBEwMBEQACEQEDEQH/xAAbAAEAAgMBAQAAAAAAAAAAAAAABAUBAgMGB//EAD4QAAEDAgQDBQYDBQgDAAAAAAEAAgMEEQUSITEGQVETImFxgTKRobHB0RRCUmJykuHwBxUWIzNTgqIkY/H/xAAaAQEAAwEBAQAAAAAAAAAAAAAAAQIDBAUG/8QAMxEBAAICAQMCAggGAwEBAAAAAAECAxEEEiExBUETUSIyYXGBobHBFEKR0eHwM1JiFSP/2gAMAwEAAhEDEQA/APuKAgICAgICAgKAQEBAQEBAQEBAQEBAQFIICAgICAgICAgICAgICAgICAgICAoBAQEBAQEBAQEBAQEBAQc5JmtLQ4gFxytB3JsTYegJ9FEzEeTTopBAQFIICAgICAgICAgICAgICAoBAQEBAQEBAQEBAQEBAQRMUxBlPE6R97CwAGrnOOgaBzJKre0VjcomdOOFUrx/mz2Mr9xuI2naNngOZ5n0VaVn61vP6fYlYrQEBAQEBSCAgICAgICAgICAgICAgICgFIICAgICgEBAQEBB5eaT8XibYxYxUje1f0M7/wDTB8hc+YWE/Tya9o7/AI+yvmXqFusICAgICAgKQQEBAQEBAQEBAQEBAQEBAQEBAQEBAQFAICDSeTK1zugLvcLoKLgrDjDTdpIbyzuNTI473fq0eFhbTqSscETFNz5nuiIegWyRB5vifjCKjJjYO2nt/osPs32Mh1y+W5+KwzZ64/tlEyhYJh1ZVntq+VzIzq2kiJjaRyz21t4XJPW2ipjrkv8ASvOo+Q9XTUzIxlY0NF72F7eg5LpiIjwl2UggICkEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBBhzgNSQPPRRM6TETPhBqq+EtcxzrhwLSBroRYrOctYbV42S3s89hvETqcNp5xmLQGMm9nO0aNLh12v4rnpnmsdM9ycPTbWSdfb7LoV8ztmMA5En+a1+JefZv8DDH80ypeJeIJImOia9glLc2SIF0wYSGlzWi5vrvY2sVjnz3pHbywzVx1j6O1bwgwNBeymLgHECSR4Li4HvEDX81xc6m11GKfeKtcfFxzSLTfvP5PVOxvKQJIyCeQcCfct/jTHmFMmHHWN9bl/iHObRNafFzrN9bKfjRPhjGLJaNxHZYwumcASYB+6Hv+oWkblSYmPLYVDmvax7R3r5XMJI0FzmafZHjcjba4vO52aSlYEBAQEBAQEBAQEBAQEBAQEBAQEBAQEHGoqAyws5zjsxgBcbbnUgAbakgajqomdCuqsXe12VsJLjsM7L+trrK2XU6iG1MNrR1a7KfEa8t1qhNGPABzAP3gVjf/26cWaKR9Cv7sxVNK9tmvYCQbFz8rvQFR9DwvHJtM73+DzvEHDsr4s9JVSGRpzCGUtfG7yuOY016rKceo3Wd/Y7/iY8v0MtI1PvHshYPj2aK0ueJzLtezUhjhuRfUt9VjbJEdnlRe/FydPmP2eN4tqjNWNe15IDWtEjbjMNza+3NWres1nbDk5/iZOqI09rwhjLW08UAcWZGlxOtrFzr2A1NzqTpqUx5Z0TntNYrDOM04leM1W9kPtGFoySPv7XaPLrkXvoAB1vurzfq7NuJbptuMfVb8o/BrTVFLDbLPA22msxefKzCVEdMe/5vSn+PyeMevwekwnGnPt+HmppT+hsgDv4XWK3pb/rMOLPhz1/5ca4peJm3yzRyMN7XDXPbf0W1c3tZy2wXjv0z+q3pK2OYExPY6xscpBLT0cNwfAraLRPhikKQQEBAQEBAQCUFNX8T00JsX5j0j73x2XPflY6++/uehg9M5GWNxXUfb2Vp46h/wBuX/r91l/G1+Uuv/4Wb/tCTTcY07987f3h9levLpPllk9H5FfGpWrMTjcMzTmHVtiFr8ak+HDPFyVnVo01OKs/TJ/D/NR8eq38Jf5w3/vFvMPHm2yn41VP4e3zj+qHiHEEcPtA33DfzH0H1Wd+TWrowen5Mv1f8KWfjYj2YRb9p1j7gFhPOn2h6NPRIn61/wAnOPjsg9+AW/ZefqFEc6feF7ehR/Lf8v8AK+wjiSCpOVpLX/ofYE/unY/NdOLk0v28S8zlem5+PHVMbj5x+64XQ88QEFJjWICmMjyRZzGi/NuUu+BzC3r4LDLk6O7r4vGnNbTx+C8QdpM46gnY7lcOHN1We/yeF0Yohevpy/Uh5vzcSSum1dvPrkivaNfgg4rQUtPEZZ2eGWPuvceQ039VW1a0ruzOKTyb9FYj75ePnFfcOpaKRrL3ySPAuPANDCD46rGszP8ALMNb+nYKxquXv+X7pNPWTaNqqJ9PnOUyEtkhe4mwDnjvBx1sHN16qb1iI3Dy82G9fM719rzuMYC9sjnds1sQ2dZrWgAai5d4LGa/Ku5cutrXheppYy6Nk8Mzi38ksfacr5Be515C+5Vox2r3mF9THhY4dwXTSSufUVEsjnEnK+zLi5s0HbQchZaRjrbzL1uJzvh16ccRt6FuHYbAMogjJ6uaJD/2upn4Fe2v3dkW5uXv1T+jWVmHyC34eNpGzmRhhHiC2xBU9WG3sno5lO/XM/fKgqqp0Ehyve+Pa7rl4HiT7Xrqua1+m3aXdTDGSneNS9dgGKsqW5XBrpAP8uQkseDyGcagf1quzBmi3aXh8/hfDnriPvevjBAAJubC52ueZsu2HlNkBSCAgICDWWQNaXOIAAJJOgAG5KiZiI3Ka1m0xEeXz/HMfkqnOZESyIadC7xP2Xk5+TbJOo8PqeHwKcaIvk73/RS2Y3QAE+Oq5no7vYzNOlgp0amPdkwt5CyI67JmGVXZOuNjuNbH3K1bTWXPyMPxa6l7SlkbIxpZqN+9dxv5rrrki0dofN5InHeYsgYri4iBazKHnc5Rew0HmVnbPPiIdXE4s5Z6reHlXTEkl2pJuSdSuaZ35e9FIiNQ2zN6KFdWc3Bh5IvE3hzfTDdm41ULxlnxZ6bAOL7FkNSLflExJ35Z7/Nehh5fit/6vG5vo+4nLg+/p/t/Z7Reg+eayvDQSdgLpM6TEbnUPj/G2NGWUtB0B1XjcrLNp0+z9L4kY6dUqfBqvspA7oufHbptt38nF8Smnr6jjs2DY2XPicrfeu+eb7RDxa+ja3Npcpse7R0ebK4sOc2Fm5+VgeQWeXlb1C+L0/piZ8b7fh/lYxcUHm4eRUxy592VvTK+0Knj7Gu1orMLQ9skcjTb9Bur25FbV1Llt6XktPTT81fhNHSV75ausuLFrIoXWIja1ouQOpNzcKvXS29zqG/8FfjRWlKRa3mZ/ssa3C8GlGV8RvtnF2vHiCErlwV7Rtb+H5s95iPu7PO1LH4e7/x5TU0x/K82nj9efqCPJUtkpvtKl/S6547R0X/KU6KrZUsEkLxfYscQ1wPRzb90+8Hqd1W+Lq7xKmHm5uHf4XKjt7T/AL5j80F9U5p3IK5+8Poa9GSu47xLV1WXe0m9rfDiPDtw/XmKobY6XWmK2rObmYYtil9roZ+0ja7qPivbpbcbfE5adFph3VmYgKQQEBB4njvFSXNpWHo6Qjx1az6+5ebzcvfoj8X0Po3FiInkW+6P3n9v6vM11S2GMN0uR6rgmfZ7GOlsl+pT/iCdbH1/r+rqYdU68LCGUWG6swmJ2kMlVdomraynau3teHYMsDb89V1YO9XzXqFotmnTznErC2b36+pXNeJi0vV9Mn/8tKtr1Xb0ZhxlnsoXiqDNXG9hb10ClrXHGu7tS4iNuahW2GfKRWtztzcwiuGemdPf8D4x+Jpg13txWjd1Lbdx3qAR5tK9fi5OumveHy/rHE+Bn3H1bd4/eP8AfaUniur7OAgbu09AtM1tVcvBx9eT7nxXEXEvcTzK8S/l9zg10xEIudZulux5CKzESmQSIpMJLNVMQytOkXFWOcy2vkreDHMbYw6MtYAfco0nJaN9kxNKbayDRRpMSp56ZzH9pC4seObdL+B6jwOi0peaoy4ceavRkjcJlPirJu5UXjk2EouWHzHL+tltM1vHd5EcTkcG024/06e9Z8/h/v4MVULmC+hGwe3Vp/n4LGa6enxeZi5EfR8+8T5hCgnLXtd0IKr4dWSsWrMPuXB9Tnh9A73r2ePbcPhefTpuv10uEQEBAQayPDWlx0ABJPgNSomdRtMRMzqHyWKczTSSv3e4v8rnQegsPReFeeq2593280jFirjr7dlTiEwM5uC6wsGjl4qkTFZ3LekT8PUdkV1Q4kZyLGw11tr08EjztpFKxHZKYLWAcDpf0tdTvupE79k+h7xUSzyTpbxw2SXHa+3qKOtDY9eQ0XVivEVeNlwTbJ2UuMP7Wxt6rK87ehxa/DVDqWwKy07YyvN1tYc5aBYDmUns9DFTdduEwFtzcn0A6kqm+7SN7dTG1lrWJ3ve5KvuPZnF7WWVHUZm2KMr11O4W/ANb2Vd2Z2la5nq272n4Eeq7OHbV9fNw+tYvicXr/6zv+vZ6LjqX2Ryy3+a6+RLxPTY8y+UYlLd5svJv5fY8emqoYcs27YPUGkqneiloWsAGi1iHHeZbyMCtplFpaMjJ2bp5qNJm0R5luYSBcpoi8TOocZNlEtIQpSqNoQ6mlzajdTE6TEu1HOWgja+jgdQfAjordUuTPw8eS3X4t847T/v3uU7Wl+jcpuNAbtOvK+oPvTytSc1IiLz1R89amPv9p/L7n1zgJ+mX9n7L1ON8ny/qXed/a9iux5QgICAgquKZclFUnrGW/xd36rHPOscuvgV6uTSPt/Tu+Z0nskrxvd9blnvpQylxmdlvva6rNYl07iKRts6oaLtyi4uC4a3KTOu0JrW099t6dlzchRDa06jyv8ADCL+ilwZ4nS0eFMw44lNgnblDXckidOe+O3VuHSbI4CxClSvXWe6NLDojat3k8Yow2W4GhF9OvNY5J7va4mTeP7UephYIyQ5t/0nNm+VkiY9k/Ev16mJ+/2VcL7X6/BXiWlon2WeGG5Kljm7QlYVNkxCld/7o2+jnBv1W+CdXiftY8qvVw8kf+Z/Lu9h/aMSG3H6fqV3crw+e9J7219r5E99yvJl9rEahlQhkKB3iNlCJTqeeyvEua9NpbagFX2wnHMJcNQ0BXi0MLUlyqa4KtrtMeGVZNU3WUy7K49OAkuoWmqVA26tEMbyTlovsraUrtBp255B5pHlplnpo+u8Ex2d/wAfsvVwR3fIc6dx+L2C6nlikEBAQU/F7L0NR+6D7nA/RY8j/il2+nTrlU+982o3XjIXj+76rNH0tqSaQse912ACxyl2Uu2aQBfvdSPoNK2idN4iJiI/3/DgHuc4uygC/LQKsREQ2rqI1tZ0777BFJqtsPbYpHlzZp7LZwWunFtzVNLto2W+yRVW0pL3aKNMohWYjECqZIdeG8w81iIs6wHoqUehW3baPS0L5X5WNaCBe98ugWlYRfPXHXdpTqFuW4O43tqFLPJPVG3TDm56+lA/34j/AAvBPyW+CN3j71ORPTxMkz/1n84e+49ps8F/Aj7L0+RG6vl/TMnTlfEiLEhePMPu4ncOjVREy2skwRLdrlBK1w/D3Si7VpSk28OTNyK451KZ/cE3RX+DZz/xuJX1UT4zZwIWdomPLpx3reNwjFyq2hq5RpbbkHKRIZPYKYZzXaNNKXFTtMViFtgNJ3gStcVdy4eZl+jqH1jhCKwe7wDV6mKHynMt4h6JdDhZQEBAQRcSp+1hlj/WxzP4gQqXr1VmvzaYcnw8lb/KYl8koNHFpuDrcHkRuvEny+1zd6xZVY5CAQSL8um6iWmCZmC92gNsBe1haw9yTEeUR27pVJFbc/NRpNsi9oY7WKRVyZL7TZTor7c8OTVWIaurd1Omcy6VDgBcqLdlaRt5fE8Ts4t26LC3d6OHF22pjaZ2ps4cjoD5HkVMfRdMzNY7JVPV9me6Sbsy6Hn1PVaWiPZjOLrjv806hYACT5pCuSe+oTeB6ftcRa4bRtfKdNNsjfi8H0XXxK7yR9jm9Wv8PhzX56j9/wBn0nGqbtYJG87XHovTvG4fJ8e/RkiXwvGqMxyu814+Wmpfd8TN10hCYsHVKSG3Csy3qXBwsqTDWJWuEYqYfmtKX6XJyeP8V6mLjoNaRlB06BdUcnUPJt6TNrb28vi2K9u69rLlyX6perx+N8KNIGdUdOmHlQtEOLnItps25SETpMo6e5uQtKxtzZsmoekwmDvBdWOHkcm+4fUMCpuzhbfc94/RehjjUPm+RfqusQtWDKAgICDRyD5lxdQ/hqovaO7ITIPP849+vqF5XJx9N/vfV+mZ/j4OifNe34eykxKESsufNcsu7HPRbSthsLZr78uaeWton2S4Z9fgm2c0XWHSZijlyRqE+sksElnjjaNFUKIltajvPXMY3MSAPur9caYxjtM6h57EsdzB4AsAch62N9R6rGZmZduPj9OpUMxLrBx7w2PXwTw64jTlmvYWIdtfqraWXFBRaIwvkTa94Yy3M7KdM8MdVns/7NsJMVO6Z470xBHhG2+X3kk+Vl6nDx9Neqff9HgeucqMmaMVfFf193ryV2PDfOON8DyvLmjuu1Hh1C4c+J9F6by+2peDlpS0rz7V0+ipli0DWqpMtJWXUaWrZxyqNL7EGESyCg3Dbqqdw2EF1aIUm6VDArRDK11rRwrasOHNd6/hfDM8gvsNSuvFTcvG5ebpq9+Au6HiS2UoEBAQCg5uQU/EWFiqhLNA4d5jujh9Dsss2L4lde7r4XKnj5Yv7e/3Pl0jXxvMbwWkGxB5FeNaNTqX2dZrkrF6zvaJV0ovpfr/APFTwtS3bu1jYQRcGynaLLOinylRMsLU2lVVSSFO9ox44iXGF+ihreEHFX3DAds1z6Ktl8Ne8yoRIC94NzmDgAN77g+9TEdobzHZ0bC9zWZmhuW4zWIeddPQaq3aFO257rSnoQ52b1ttqk+ezKckxGlrmETbm2mwRhETedQcPYS6vqBmB7JpBkdtp+keJ+Vyt8GGclte3uc3lV4eHt9afH9/wfWmtAAAAAAsANAANgF7MRp8TMzM7lq4ohDxGnbNGWO9D0PVVtXqjTTFknHbcPmeMYWY3kELzsuPUvpeLyYvXcKSWnsueavSrk2jvjVJhrFnIsVV+poY0TFjs1Glups2JNImzu2NTpTql0a1TpWZd4WXVohla2oelwfDy8gAankurHR5XIzRD6JhNCIWAczufou+ldQ8DNl67bTwtGDKAgICAg0cEHGQIPN8S4C2pGYWbIBo7kfB33XNn48ZO8eXpcD1C3GnU96/L+z55VMfA8xytII6/O/MLyr0ms6l9bitTPXrxztIhc1wFiqs71tDYxqNIi2m1r6IRPuliENbdW0xnJudK3EIA8W5bqkx3dGK8wgwUuUENaB1P5j5ndWWtfc7mUiGl66ppWciQ+drB48giK45vLrg+DTV8nNsYPelI0Hg0fmd8ua3w4LZJ7ePmpyubh4VPnb2j+/yh9Rwugjp42xRNs0epJ5uceZK9alIpXUPjeRyL58k5Lz3S1di5uQRpkHn8apRINd+RVL0i0N8Ge2K248PG11KWnUfZefkxzXy+i43JrkjcSrJGLnmHoVsjuYqTDWLOZYoW2ZUTtkNRG3ZjUVmXeOO6tEM7X0u8Hwt0jhYfZdGLFNnm8rl1pHeX0LCKFsLRbU8z9l6NMcVfP5uRbJK3aVo524QZQEBAQEGCEHJ7UEWVqCkxigjmblkaD0PMeR5Kl8dbxq0N+PycuC3VjnTwlfww+NxMMgI6Pu1w9RofguC/Ct/LL6TB6/itGs1dT847whATs0c2/iCFhPGy/J1/wD0ODbvF9fhLswO0OoSONl+TG/qHFj+eHWoqXEWs7fewA+atPHyx7M8fN4sz9b9Ud8pG9/cVjNLR5h20nHf6ton8XBtaR7IPuVYq3nHX3l0iZPJ7LSL/wDEfFa1wXt4hhk5XEw/WtH6/o9DgvC4JDp3F/PING+p3K7MXD13u8fl+uTMdOCNfbPl7yjjDWhrQABoGgWAHgF2xERGoeBa02ndp3KcwKVWxCDRwQR5WoKyriug81ilLcHRRMRMalal7UndZ08zVUrgdFyX4u/qvXweq67ZI/GEJ7SNwVy2wXj2epj52G/i39ezQhZzSYdNc1Z8SxlVelp1t2MTolS2WISoacnYFa0wXt7OTLz8VPNltQ4fqLrrx8XX1nlZ/VJt2pD1uFxBoAAsuuIiO0PKtebTuy/gClVMYg6BBlAQEBAQEGpCDi9iCHPTXQVVVht0FXNghKDm3h8oOn+H0GP8PoOkeAIncp1PgoCIWkFIGoJbGWQdAg3BQYKDi8IIk0aCrq6W6CmqcPvyQV0uGeCDg7DPBE7kbhngPco6Y+S0ZLx7y7x4d4JqFZtM+ZTIaDwUoWVNRoLmkp7ILSJiCQ0INkGUBAQEBAQYQYIQaOag4vjQcjCEGOzQMiBlQLIMgINwEGwQZQbBAQauCDi9qCNLEgiyU6Dg6kCDQ0QQYFEEHRtEEHZlIEEqKnQTIYkEprUG4QZQEBAQEBAQEGEGCg0cEGhag1LUGMqDFkGLIFkGwQZQEGwQZQYKDQhBzcxBydGg17NBjskGwiQbCFB0bEg6NjQd2tQbgIMoCAgICAgICAgIMINSEGpCDBCDUhBghBjKgZUCyBZAsg2sgIMoFkGC1BqWIMdmgZEGQxBsGINw1BsGoNrIMoCAgICAgICAgICAgwgxZBiyBZBjKgxlQMqDFkDKgZUCyDNkCyDNkCyBZAyoM5UCyDNkGbIMoCAgICAgICD/2Q==")).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.e("Success", "Successfully Inserted");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Failed", e.getMessage());
            }
        });


    }

    public void getDatabaseItems() {


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("items");

        FirebaseRecyclerOptions<Item> options =
                new FirebaseRecyclerOptions.Builder<Item>()
                        .setQuery(query, Item.class)

                        .build();

        Log.e("Ref", String.valueOf(options.getSnapshots()));

        adapter = new FirebaseRecyclerAdapter<Item, ItemsViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull ItemsViewHolder holder, int position, @NonNull Item model) {
                holder.tv_item_price.setText(Integer.toString(model.getItemPrice()));
                holder.tv_item_description.setText(model.getItemDescription());
                holder.tv_item_name.setText(model.getItemName());

                Picasso.with(getContext())
                        .load(model.getImage())
                        .placeholder(R.drawable.placeholder)
                        .into(holder.img_image);

                holder.btn_select.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int total = Integer.parseInt(((MainActivity)getActivity()).cart_price.getText().toString()) + model.getItemPrice();
                        ((MainActivity)getActivity()).cart_price.setText(Integer.toString(total));

                        selectedItems.add(model);

                        Log.e("00", Integer.toString(selectedItems.size()));
                    }
                });
            }

            @NonNull
            @Override
            public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.single_item_view, parent, false);

                return new ItemsViewHolder(view);
            }

        };

        adaptor.notifyDataSetChanged();
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                items.clear();
//
//                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
//                    Item newItem = snapshot.getValue(Item.class);
//                    items.add(newItem);
//                    Log.i("Mysnapshot", snapshot.toString());
//
//                }
//
//                    adaptor.notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }


    private class ItemsViewHolder extends RecyclerView.ViewHolder {

        public View view;
        ImageView img_image;
        TextView tv_item_name,tv_item_description, tv_item_price;
        Button btn_select;

        public ItemsViewHolder(View v) {
            super(v);
            view = v;
            img_image = v.findViewById(R.id.img_image);
            tv_item_name = v.findViewById(R.id.tv_item_name);
            tv_item_description = v.findViewById(R.id.tv_item_description);
            tv_item_price = v.findViewById(R.id.tv_item_price);
            btn_select = v.findViewById(R.id.btn_select);

        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class ItemsAdaptor extends RecyclerView.Adapter<ItemsAdaptor.ViewHolder> {


        private ArrayList<Item> mDataset;

        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public View view;
            ImageView img_image;
            TextView tv_item_name,tv_item_description, tv_item_price;
            Button btn_select;

            public ViewHolder(View v) {
                super(v);
                img_image = v.findViewById(R.id.img_image);
                tv_item_name = v.findViewById(R.id.tv_item_name);
                tv_item_description = v.findViewById(R.id.tv_item_description);
                tv_item_price = v.findViewById(R.id.tv_item_price);
                btn_select = v.findViewById(R.id.btn_select);

            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public ItemsAdaptor(ArrayList<Item> myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ItemsAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                         int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.single_item_view, parent, false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tv_item_price.setText(Integer.toString(mDataset.get(position).getItemPrice()));
            holder.tv_item_description.setText(mDataset.get(position).getItemDescription());
            holder.tv_item_name.setText(mDataset.get(position).getItemName());

            Picasso.with(getContext())
                    .load(mDataset.get(position).getImage())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.img_image);

            holder.btn_select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int total = Integer.parseInt(((MainActivity)getActivity()).cart_price.getText().toString()) + mDataset.get(position).getItemPrice();
                    ((MainActivity)getActivity()).cart_price.setText(Integer.toString(total));

                    selectedItems.add(mDataset.get(position));
                    }
            });

        }
        // Replace the contents of a view (invoked by the layout manager)
        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapter != null) {
            adapter.stopListening();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
