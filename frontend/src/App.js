import React, { useEffect, useState } from "react";

function App() {
    const [message, setMessage] = useState("");

    useEffect(() => {
        fetch("/hello")
            .then((res) => res.text())
            .then((data) => setMessage(data));
    }, []);

    return <div>{message}</div>;
}

export default App;
