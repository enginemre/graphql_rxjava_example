package com.engin.graphqlex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.apollographql.apollo3.api.ApolloResponse
import com.engin.graphqlex.repository.UserRepo
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.observers.DisposableSingleObserver

class MainActivity : AppCompatActivity() {

    private val disposable: CompositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.text1)
        textView.setOnClickListener {
//            getUsers(25)
//            getUserById("4026dd5e-819f-4670-a0ea-6725f60b4d9d")
//            deleteUser(id = "c0ee4268-203f-458b-a544-705a85dd8979")
//            createUser("Salatalık","Patlıcan","salpat")
//            updateUser(id = "4026dd5e-819f-4670-a0ea-6725f60b4d9d", name = "HHH", rockets = "SEsl", twitter = "sdfsf")


        }


    }


    private fun getUsers(limit: Int = 10) {
        disposable.add(
            UserRepo.getUserList(limit).subscribeWith(object :
                DisposableSingleObserver<ApolloResponse<UsersListQuery.Data>>() {
                override fun onSuccess(t: ApolloResponse<UsersListQuery.Data>) {
                    for (element in t.data?.users!!)
                        Log.d("userList", "Başarılı : ${element.name}")
                }

                override fun onError(e: Throwable) {
                    Log.d("userList", "Sıkıntılı : ${e.toString()}")
                }

            })
        )

    }

    private fun getUserById(id: String) {
        disposable.add(
            UserRepo.getUserById(id).subscribeWith(object :
                DisposableSingleObserver<ApolloResponse<UserByIdQuery.Data>>() {
                override fun onSuccess(t: ApolloResponse<UserByIdQuery.Data>) {
                    Log.d("UserById", "Success updated Name : ${t.data?.users_by_pk!!.name}")
                }

                override fun onError(e: Throwable) {
                    Log.d("UserById", "Failed ${e.toString()}")
                }

            })
        )
    }

    private fun deleteUser(id: String) {
        disposable.add(UserRepo.deleteUser(id).subscribeWith(object :
            DisposableSingleObserver<ApolloResponse<DeleteUserMutation.Data>>() {
            override fun onSuccess(t: ApolloResponse<DeleteUserMutation.Data>) {
                Log.d(
                    "deleteUser",
                    "Success deleted rows  : ${t.data?.delete_users!!.affected_rows.toString()}"
                )
            }

            override fun onError(e: Throwable) {
                Log.d("deleteUser", "Failed ${e.toString()}")
            }


        }))
    }

    private fun updateUser(id: String,name: String,rockets: String,twitter: String){
        disposable.add(
            UserRepo.updateUser(name,rockets,twitter,id).subscribeWith(object: DisposableSingleObserver<ApolloResponse<UpdateUserMutation.Data>>(){
                override fun onSuccess(t: ApolloResponse<UpdateUserMutation.Data>) {
                    Log.d("UpdateUser","Updated User Name : ${t.data?.update_users!!.returning[0].name}")
                }

                override fun onError(e: Throwable) {
                    Log.d("deleteUser", "Failed ${e.toString()}")
                }

            })
        )
    }

    private fun createUser(name: String, rockets: String, twitter: String) {
        disposable.add(
            UserRepo.createUser(name, rockets, twitter).subscribeWith(object :
                DisposableSingleObserver<ApolloResponse<InsertUserMutation.Data>>() {
                override fun onSuccess(t: ApolloResponse<InsertUserMutation.Data>) {
                    Log.d(
                        "createUser",
                        "Affected Rows: ${t.data?.insert_users!!.affected_rows.toString()}"
                    )
                }

                override fun onError(e: Throwable) {
                    Log.d("createUser", "Başarısız ${e.toString()}")
                }

            })
        )
    }
}