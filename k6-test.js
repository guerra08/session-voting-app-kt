import http from 'k6/http';

export default function () {
    const url = 'http://localhost:8080/api/v1/votes/schedule/1';

    const payload = (id) => JSON.stringify({
        cpf: '37234355057',
        userId: id,
        option: Math.random() > 0.5 ? 'YES' : 'NO'
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    for (let i= 1; i < 10000; i++) {
        http.post(url, payload(i), params);
    }

}