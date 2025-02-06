import React from "react";
import Header from "../components/Header";

const Users = () => {
    return (
        <div className="admin-panel">
            <Header />
            <main>
                <h1>Users</h1>
                <div className="card-container">
                    <div className="card">
                        <h2>Users</h2>
                        <button className="button">Check</button>
                    </div>
                </div>
            </main>
        </div>
    );
}

export default Users;
