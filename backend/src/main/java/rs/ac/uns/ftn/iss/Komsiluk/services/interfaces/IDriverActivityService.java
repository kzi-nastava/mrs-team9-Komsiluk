package rs.ac.uns.ftn.iss.Komsiluk.services.interfaces;

import rs.ac.uns.ftn.iss.Komsiluk.beans.User;

public interface IDriverActivityService {

    public void startActivity(User driver);

    public void endActivity(User driver);

    public long getWorkedMinutesLast24h(User driver);

    public boolean canAcceptNewRide(User driver);
    
    public boolean canAcceptNewRide(Long driverId);
}