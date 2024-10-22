#include "raylib.h"
#include "raymath.h"
#include <iostream>
#include <vector>
#include <external/stb_vorbis.c>
#include <cmath>
#include <algorithm>
#include <string>

using namespace std;

float simulationStartTime = GetTime();
float simulationDuration = 15.0f;  // Run simulation for 15 seconds

class Target { //shrimp
public:
    Vector2 posTarget;
    Texture2D texture;
    int pointValue;
    Target(Vector2 pos, Texture2D& tex, int pts) {
        posTarget = pos;
        texture = tex;
        pointValue = pts;
    }
};

// Colors based on point value, cool colors are lesser points, warm color are more points, black is most points (10)
Color getColorfromPoints(Target target) {
    
    switch (target.pointValue) {

    case 1: return PURPLE;
        break;
    case 2: return DARKBLUE;
        break;
    case 3: return SKYBLUE;
        break;
    case 4: return DARKGREEN;
        break;
    case 5: return GREEN;
        break;
    case 6: return YELLOW;
        break;
    case 7: return ORANGE;
        break;
    case 8: return RED;
        break;
    case 9: return PINK;
        break;
    case 10: return BLACK;
        break;
    default: return WHITE;
    }
}

// Class for Shiyona's Agent
class Pufferfish {
public:
    Vector2 posAgent;
    Vector2 velocity;
    Vector2 acceleration;

    float maxSpeed;
    float maxForce;
    float angle;
    float scale = 1;

    Vector2 currentTarget;
    int foodEaten = 0;
    float radius;
    Texture2D texture;
    bool hasEaten = false;
    float timeSinceLastEat = 0.0f;
    float eatDuration = 0.25f;
    Color color;

    //increases pufferfish's size
    void increaseSize(float multiplier) {
        scale *= multiplier;
    }

    void increaseSpeed(float multiplier) {
        maxSpeed *= multiplier;
    }

    //agent eats food
    void eatFood(Target target) {
        foodEaten += target.pointValue;
        hasEaten = true;
        color = BLUE;
        timeSinceLastEat = GetTime();
    }

    Pufferfish(Vector2 pos, Vector2 vel, Vector2 acc, float maxS, float maxF, float ang, float rad, Texture2D tex, Color col) {
        posAgent = pos;
        velocity = vel;
        acceleration = acc;
        maxSpeed = maxS;
        maxForce = maxF;
        angle = ang;
        radius = rad;
        texture = tex;
        color = col;
    }
};

// Class for Nethra's Agent
class Anglerfish {
public:
    Vector2 posAgent;
    Vector2 velocity;
    Vector2 acceleration;

    float maxSpeed;
    float maxForce;
    float angle;
    float size;
    Vector2 currentTarget;
    int foodEaten = 0;
    float radius; //radius of the AGENT --> the area that can consume a food
    Texture2D texture;
    bool hasEaten = false;
    Color color;
    float seekRadius = 100.0; // radius of SEEKING --> the area where the anglerfish is able to see the food

    bool isSeeking = false;
    float timeSinceLastEat = 0.0f;
    float eatDuration = 0.25f;

    //agent eats food
    void eatFood(Target target) {
        isSeeking = false;
        foodEaten += target.pointValue;
        hasEaten = true;
        color = PINK;
        timeSinceLastEat = GetTime();
    }

    // increases SEEKING radius for evolution
    void increaseRadius(float multiplier) {
        seekRadius *= multiplier;
    }

    void increaseSpeed(float multiplier) {
        maxSpeed *= multiplier;
    }

    Anglerfish(Vector2 pos, Vector2 vel, Vector2 acc, float maxS, float maxF, float ang, float rad, Texture2D tex, Color col) {
        posAgent = pos;
        velocity = vel;
        acceleration = acc;
        maxSpeed = maxS;
        maxForce = maxF;
        angle = ang;
        radius = rad;
        texture = tex;
        color = col;
    }

};

class Anglepuff {
public:
    Vector2 posAgent;
    Vector2 velocity;
    Vector2 acceleration;

    float maxSpeed;
    float maxForce;
    float angle;
    float size;
    float radius;
    Vector2 currentTarget;
    int foodEaten = 0;

    float scale; // uses pufferfish's scale
    float seekRadius = 100.0;  // uses anglerfish's seek radius

    Texture2D texture;

    Color color;

    bool isSeeking = false;
    bool hasEaten = false;
    float timeSinceLastEat = 0.0f;
    float eatDuration = 0.25f;

    //  increases scale/size of the agent
    void increaseSize(float multiplier) {
        scale *= multiplier;
    }

    void increaseSpeed(float multiplier) {
        maxSpeed *= multiplier;
    }

    //  increases seeking radius of the agent
    void increaseRadius(float multiplier) {
        seekRadius *= multiplier;
    }

    //it eats food...... again
    void eatFood(Target target) {
        isSeeking = false;
        foodEaten += target.pointValue;
        hasEaten = true;
        color = PINK;
        timeSinceLastEat = GetTime();
    }

    Anglepuff(Vector2 pos, Vector2 vel, Vector2 acc, float maxS, float maxF, float ang, float rad, Texture2D tex, Color col) {
        posAgent = pos;
        velocity = vel;
        acceleration = acc;
        maxSpeed = maxS;
        maxForce = maxF;
        angle = ang;
        radius = rad;
        texture = tex;
        color = col;
    }
};

// Function for the Seek behavior (to make agents move towards the target)
Vector2 Seek(Pufferfish& agent, Vector2 target) {
    Vector2 desired = Vector2Subtract(target, agent.posAgent);
    desired = Vector2Normalize(desired);
    desired = Vector2Scale(desired, agent.maxSpeed);

    Vector2 steer = Vector2Subtract(desired, agent.velocity);

    if (Vector2Length(steer) > agent.maxForce) {
        steer = Vector2Scale(Vector2Normalize(steer), agent.maxForce);
    }

    return steer;
}

Vector2 Seek(Anglerfish& agent, Vector2 target) {
    Vector2 desired = Vector2Subtract(target, agent.posAgent);
    desired = Vector2Normalize(desired);
    desired = Vector2Scale(desired, agent.maxSpeed);

    Vector2 steer = Vector2Subtract(desired, agent.velocity);

    if (Vector2Length(steer) > agent.maxForce) {
        steer = Vector2Scale(Vector2Normalize(steer), agent.maxForce);
    }

    return steer;
}

Vector2 Seek(Anglepuff& agent, Vector2 target) {
    Vector2 desired = Vector2Subtract(target, agent.posAgent);
    desired = Vector2Normalize(desired);
    desired = Vector2Scale(desired, agent.maxSpeed);

    Vector2 steer = Vector2Subtract(desired, agent.velocity);

    if (Vector2Length(steer) > agent.maxForce) {
        steer = Vector2Scale(Vector2Normalize(steer), agent.maxForce);
    }

    return steer;
}


//generates a random point on the screen for each agent to "wander"
Vector2 generatePoint(Pufferfish& agent) {
    float x = (float)GetRandomValue(0, 2000);
    float y = (float)GetRandomValue(0, 1125);

    return Vector2{ x, y };
}

Vector2 generatePoint(Anglerfish& agent) {
    float x = (float)GetRandomValue(0, 2000);
    float y = (float)GetRandomValue(0, 1125);

    return Vector2{ x, y };
}

Vector2 generatePoint(Anglepuff& agent) {
    float x = (float)GetRandomValue(0, 2000);
    float y = (float)GetRandomValue(0, 1125);

    return Vector2{ x, y };
}

// sort the agents based on how much food eaten from greatest to least
void sortAgents(vector<Pufferfish>& agents) {
    sort(agents.begin(), agents.end(), [](const Pufferfish& a, const Pufferfish& b) {
        return a.foodEaten > b.foodEaten;
        });
}


void sortAgents(vector<Anglerfish>& agents) {
    sort(agents.begin(), agents.end(), [](const Anglerfish& a, const Anglerfish& b) {
        return a.foodEaten > b.foodEaten;
        });
}

void sortAgents(vector<Anglepuff>& agents) {
    sort(agents.begin(), agents.end(), [](const Anglepuff& a, const Anglepuff& b) {
        return a.foodEaten > b.foodEaten;
        });
}

// Update agent movement based on steering behavior
void UpdateAgent(Pufferfish& agent, Vector2 target) {
    agent.acceleration = Seek(agent, target);
    agent.velocity = Vector2Add(agent.velocity, agent.acceleration);

    if (Vector2Length(agent.velocity) > agent.maxSpeed) {
        agent.velocity = Vector2Scale(Vector2Normalize(agent.velocity), agent.maxSpeed);
    }

    agent.posAgent = Vector2Add(agent.posAgent, agent.velocity);
    agent.angle = atan2(agent.velocity.y, agent.velocity.x) * (180.0f / PI);
}

void UpdateAgent(Anglerfish& agent, Vector2 target) {

    agent.acceleration = Seek(agent, target);
    agent.velocity = Vector2Add(agent.velocity, agent.acceleration);

    if (Vector2Length(agent.velocity) > agent.maxSpeed) {
        agent.velocity = Vector2Scale(Vector2Normalize(agent.velocity), agent.maxSpeed);
    }

    agent.posAgent = Vector2Add(agent.posAgent, agent.velocity);
    agent.angle = atan2(agent.velocity.y, agent.velocity.x) * (180.0f / PI);
}

void UpdateAgent(Anglepuff& agent, Vector2 target) {

    agent.acceleration = Seek(agent, target);
    agent.velocity = Vector2Add(agent.velocity, agent.acceleration);

    if (Vector2Length(agent.velocity) > agent.maxSpeed) {
        agent.velocity = Vector2Scale(Vector2Normalize(agent.velocity), agent.maxSpeed);
    }

    agent.posAgent = Vector2Add(agent.posAgent, agent.velocity);
    agent.angle = atan2(agent.velocity.y, agent.velocity.x) * (180.0f / PI);
}

// calculates if the length between the positions of target and agent is less than the SEEKING radius length
float distanceBetween(Anglerfish agent, Target target) {
    if (Vector2Length(Vector2Subtract(target.posTarget, agent.posAgent)) <= agent.seekRadius) {
        agent.isSeeking = true;
        Seek(agent, target.posTarget);
    }
    return Vector2Length(Vector2Subtract(target.posTarget, agent.posAgent));
}

float distanceBetween(Anglepuff agent, Target target) {
    if (Vector2Length(Vector2Subtract(target.posTarget, agent.posAgent)) <= agent.seekRadius) {
        agent.isSeeking = true;
        Seek(agent, target.posTarget);
    }
    return Vector2Length(Vector2Subtract(target.posTarget, agent.posAgent));
}

// evolves the agents based on their food eaten
// major: first --> 2x   second --> 1.6x  third --> 1.3x  last --> deleted
// minor: increases speed; first --> 1.5x   second --> 1.3x  third --> 1.0x  last --> deleted

void evolveAgents(vector<Pufferfish>& puffers, vector<Anglerfish>& anglers, vector<Anglepuff>& anglepuffs) {
    sortAgents(puffers);

    if (!puffers.empty()) {

        puffers.pop_back();

        puffers[0].increaseSize(2.0f);
        puffers[0].radius *= 2.0f;
        puffers[0].increaseSpeed(1.5f);

        if (puffers.size() > 1) {
            puffers[1].increaseSize(1.6f);
            puffers[1].radius *= 1.6f;
            puffers[1].increaseSpeed(1.3f);
        }

        if (puffers.size() > 2) {
            puffers[2].increaseSize(1.3f);
            puffers[2].radius *= 1.3f;
            puffers[2].increaseSpeed(1.0f);
        }

    }

    sortAgents(anglers);
    if (!anglers.empty()) {
        anglers.pop_back();

        anglers[0].increaseRadius(2.0f);
        anglers[0].increaseSpeed(1.5f);

        if (anglers.size() > 1) {
            anglers[1].increaseRadius(1.6f);
            anglers[1].increaseSpeed(1.3f);
        }

        if (anglers.size() > 2) {
            anglers[2].increaseRadius(1.3f);
            anglers[2].increaseSpeed(1.0f);
        }
    }

    sortAgents(anglepuffs);
    if (!anglepuffs.empty()) {
        anglepuffs.pop_back();

        anglepuffs[0].increaseRadius(2.0f);
        anglepuffs[0].increaseSize(2.0f);
        anglepuffs[0].radius *= 2.0f;
        anglepuffs[0].increaseSpeed(1.5f);

        if (anglepuffs.size() > 1) {
            anglepuffs[1].increaseRadius(1.6f);
            anglepuffs[1].increaseSize(1.6f);
            anglepuffs[1].radius *= 1.6f;
            anglepuffs[1].increaseSpeed(1.3f);
        }

        if (anglepuffs.size() > 2) {
            anglepuffs[2].increaseRadius(1.3f);
            anglepuffs[2].increaseSize(1.3f);
            anglepuffs[2].radius *= 1.3f;
            anglepuffs[2].increaseSpeed(1.0f);
        }
    }
}

vector<Target> resetFood(Texture2D foodTexture) {
    vector<Target> foods;
    const int numFoods = 50;
    for (int i = 0; i < numFoods; i++) {
        Target food = Target({ (float)GetRandomValue(0, GetScreenWidth()), (float)GetRandomValue(0, GetScreenHeight()) }, foodTexture, (int)GetRandomValue(1, 10));
        foods.push_back(food);
    }
    return foods;
}


int main(void) {
    InitWindow(2000, 1125, "Shiyona and Nethra's Fight to the Death");
    SetTargetFPS(60);
    float pufferfishWanderTimer = GetTime();
    float anglerfishWanderTimer = GetTime();
    float anglepuffWanderTimer = GetTime();

    Image puffImage = LoadImage("C:\\Users\\shiyo\\OneDrive\\Desktop\\ProjectFilesRL\\raylib-game-template-main\\projects\\VS2022\\pufferfish.png");
    ImageResize(&puffImage, 70, 70);

    Image angImage = LoadImage("C:\\Users\\shiyo\\OneDrive\\Desktop\\ProjectFilesRL\\raylib-game-template-main\\projects\\VS2022\\anglerfish.png");
    ImageResize(&angImage, 100, 70);

    Image foodImage = LoadImage("C:\\Users\\shiyo\\OneDrive\\Desktop\\ProjectFilesRL\\raylib-game-template-main\\projects\\VS2022\\shrimp.png");
    ImageResize(&foodImage, 50, 50);

    Image mutantImage = LoadImage("C:\\Users\\shiyo\\Downloads\\anglepuff.png");
    ImageResize(&mutantImage, 70, 70);


    Texture2D puffTexture = LoadTextureFromImage(puffImage);
    Texture2D angTexture = LoadTextureFromImage(angImage);
    Texture2D foodTexture = LoadTextureFromImage(foodImage);
    Texture2D mutantTexture = LoadTextureFromImage(mutantImage);

    vector<Pufferfish> puffers;
    vector<Anglerfish> anglers;
    vector<Target> foods;
    vector<Vector2> wanderingTargets;
    vector<Anglepuff> anglepuffs;
    int numPuffs = 4;
    int numAngs = 4;
    int round = 1;

    //initializing both agents into lists
    for (int i = numPuffs - 1; i >= 0; i--){
        Pufferfish agent({ (float)GetRandomValue(100, 800), (float)GetRandomValue(100, 600) }, { 0, 0 }, { 0, 0 }, 3.0f, 0.1f, 0.0f, 20, puffTexture, WHITE);
        puffers.push_back(agent);
    }

    for (int i = numAngs - 1; i >= 0; i--){
        Anglerfish agent({ (float)GetRandomValue(100, 800), (float)GetRandomValue(100, 600) }, { 0, 0 }, { 0, 0 }, 3.0f, 0.1f, 0.0f, 40, angTexture, WHITE);
        anglers.push_back(agent);
    }

    //start wandering at a random point
    for (int i = 0; i < puffers.size(); i++) {
        puffers.at(i).currentTarget = generatePoint(puffers.at(i));
    }

    for (int i = 0; i < anglers.size(); i++) {
        if (anglers.at(i).isSeeking != true) {
            anglers.at(i).currentTarget = generatePoint(anglers.at(i));
        }
    }

    for (int i = 0; i < anglepuffs.size(); i++) {
        if (anglepuffs.at(i).isSeeking != true) {
            anglepuffs.at(i).currentTarget = generatePoint(anglepuffs.at(i));
        }
    }

    // Create 50 food targets
    const int numFoods = 50;
    //for (int i = 0; i < numFoods; i++) {
    for (int i = numFoods - 1; i >= 0; i--) {
        Target food = Target({ (float)GetRandomValue(0, GetScreenWidth()), (float)GetRandomValue(0, GetScreenHeight()) }, foodTexture, (int)GetRandomValue(1, 10));
        foods.push_back(food);
    }

    while (!WindowShouldClose()) {

        // to end the round if the round is more than 3 rounds
        if (round > 4) {
            BeginDrawing();
            ClearBackground(BLUE);

            // Display end of game message
            DrawTextPro(GetFontDefault(), "Game Over! Thanks for playing!", { 200, 500 }, { 0, 0 }, 0, 50.0f, 6.0f, PINK);

            EndDrawing();
            //continue;
            break;
        }


        if (GetTime() - pufferfishWanderTimer >= 1.0f) {
            for (Pufferfish& agent : puffers) {
                agent.currentTarget = generatePoint(agent);
            }
            pufferfishWanderTimer = GetTime();
        }

        // Anglerfish wandering update every 1 sec
        if (GetTime() - anglerfishWanderTimer >= 1.0f) {
            for (Anglerfish& agent : anglers) {
                if (!agent.isSeeking) {
                    agent.currentTarget = generatePoint(agent);
                }
            }
            anglerfishWanderTimer = GetTime();
        }

        if (GetTime() - anglepuffWanderTimer >= 1.0f) {
            for (Anglepuff& agent : anglepuffs) {
                if (!agent.isSeeking) {
                    agent.currentTarget = generatePoint(agent);
                }
            }
            anglepuffWanderTimer = GetTime();
        }

        if (!puffers.empty()) {
            for (Pufferfish& agent : puffers) {
                if (!foods.empty()) {
                    Vector2 closestFoodPos = foods[0].posTarget;
                    float closestDistance = Vector2Distance(agent.posAgent, closestFoodPos);
                    int foodIndex = 0;

                    for (int i = 0; i < foods.size(); i++) {
                        Target& food = foods[i];
                        float distance = Vector2Distance(agent.posAgent, food.posTarget);

                        // finding a food
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestFoodPos = food.posTarget;
                            foodIndex = i;
                        }

                        // Only "eat" food if it's within the detection radius (no seeking)
                        if (Vector2Distance(agent.posAgent, food.posTarget) <= agent.radius + 20) {
                            agent.eatFood(food);
                            foods.erase(foods.begin() + foodIndex);
                            break;
                        }
                    }
                }
                if (GetTime() - agent.timeSinceLastEat >= agent.eatDuration) {
                    agent.color = WHITE;
                }

                UpdateAgent(agent, agent.currentTarget);
            }
        }

        if (!anglers.empty()) {
            for (Anglerfish& agent : anglers) {
                bool foundFoodInRange = false; 
                Vector2 closestFoodPos = agent.currentTarget;
                float closestDistance = Vector2Distance(agent.posAgent, closestFoodPos);
                int foodIndex = 0;

                // Check if any food is within the seek radius
                for (int i = 0; i < foods.size(); i++) {
                    Target& food = foods[i];
                    float distance = Vector2Distance(agent.posAgent, food.posTarget);

                    if (distance <= agent.seekRadius) {

                        foundFoodInRange = true;

                        // Find the closest food in range
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestFoodPos = food.posTarget;
                            foodIndex = i;
                        }

                        if (distance <= agent.radius + 10) {
                            agent.eatFood(food);
                            foods.erase(foods.begin() + foodIndex);
                            break; 
                        }
                    }
                }

                if (!foundFoodInRange) {

                    if (GetTime() - anglerfishWanderTimer >= 1.0f) {
                        agent.currentTarget = generatePoint(agent); 
                        anglerfishWanderTimer = GetTime();
                    }
                    closestFoodPos = agent.currentTarget;
                }

                if (GetTime() - agent.timeSinceLastEat >= agent.eatDuration) {
                    agent.color = WHITE; 
                }

                UpdateAgent(agent, closestFoodPos);
            }
        }

        if (!anglepuffs.empty()) {
            for (Anglepuff& agent : anglepuffs) {
                bool foundFoodInRange = false; 
                Vector2 closestFoodPos = agent.currentTarget; 
                float closestDistance = Vector2Distance(agent.posAgent, closestFoodPos);
                int foodIndex = 0;

                for (int i = 0; i < foods.size(); i++) {
                    Target& food = foods[i];
                    float distance = Vector2Distance(agent.posAgent, food.posTarget);

                    if (distance <= agent.seekRadius) {

                        foundFoodInRange = true;

                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestFoodPos = food.posTarget;
                            foodIndex = i;
                        }

                        if (distance <= agent.radius + 10) {
                            agent.eatFood(food);
                            foods.erase(foods.begin() + foodIndex); 
                            break;
                        }
                    }
                }

                if (!foundFoodInRange) {

                    if (GetTime() - anglepuffWanderTimer >= 1.0f) {
                        agent.currentTarget = generatePoint(agent);  // Generate a new wandering target
                        anglepuffWanderTimer = GetTime();
                    }
                    closestFoodPos = agent.currentTarget;  // Set wandering target as the current target
                }

                if (GetTime() - agent.timeSinceLastEat >= agent.eatDuration) {
                    agent.color = WHITE;
                }

                UpdateAgent(agent, closestFoodPos);
            }
        }

        if (GetTime() - simulationStartTime >= simulationDuration) {

            evolveAgents(puffers, anglers, anglepuffs);
            round++;
            foods = resetFood(foodTexture);
            simulationStartTime = GetTime();

            float mutationRange = 120.0; 
            vector<int> puffersToRemove;
            vector<int> anglersToRemove;

            for (int i = 0; i < puffers.size(); i++) {
                Pufferfish& puffer = puffers[i];

                for (int j = 0; j < anglers.size(); j++) {
                    Anglerfish& angler = anglers[j];

                    float distance = Vector2Distance(puffer.posAgent, angler.posAgent);
                    
                    if (distance < mutationRange) {

                        // Create the hybrid Anglepuff
                        Anglepuff hybrid(
                            { (puffer.posAgent.x + angler.posAgent.x) / 2, (puffer.posAgent.y + angler.posAgent.y) / 2 },
                            { 0, 0 }, { 0, 0 }, 4.0f, 0.2f, 0.0f, 70.0f, mutantTexture, WHITE
                        );

                        anglepuffs.push_back(hybrid);


                        puffers.erase(puffers.begin() + i);
                        anglers.erase(anglers.begin() + j);


                        break;
                    }
                }
            }
        }

        // Drawing section
        BeginDrawing();
        ClearBackground(BLUE);  // Ocean background color


        string roundText = "Round: " + to_string(round);
        DrawTextPro(GetFontDefault(), roundText.c_str(), { 50, 50 }, { 25, 25 }, 0, 70.0, 6.0, PINK);

        for (const Pufferfish& agent : puffers) {
            DrawTexturePro(agent.texture, { 0, 0, (float)agent.texture.width, (float)agent.texture.height },
                { agent.posAgent.x, agent.posAgent.y, (float)agent.texture.width * agent.scale, (float)agent.texture.height * agent.scale },
                { (float)agent.texture.width / 2 * agent.scale, (float)agent.texture.height / 2 * agent.scale }, agent.angle, agent.color);
            DrawCircleLines(agent.posAgent.x, agent.posAgent.y, agent.radius, YELLOW);
        }

        for (const Anglerfish& agent : anglers) {
            DrawTexturePro(agent.texture, { 0, 0, (float)agent.texture.width, (float)agent.texture.height },
                { agent.posAgent.x, agent.posAgent.y, (float)agent.texture.width, (float)agent.texture.height },
                { (float)agent.texture.width / 2, (float)agent.texture.height / 2 }, agent.angle, agent.color);
            DrawCircleLines(agent.posAgent.x, agent.posAgent.y, agent.seekRadius, YELLOW);
            DrawCircleLines(agent.posAgent.x, agent.posAgent.y, agent.radius, RED);
        }

        for (Anglepuff& agent : anglepuffs) {
            DrawTexturePro(agent.texture, { 0, 0, (float)agent.texture.width, (float)agent.texture.height },
                { agent.posAgent.x, agent.posAgent.y, (float)agent.texture.width, (float)agent.texture.height },
                { (float)agent.texture.width / 2, (float)agent.texture.height / 2 }, agent.angle, agent.color);
            DrawCircleLines(agent.posAgent.x, agent.posAgent.y, agent.radius, RED);
        }


        if (round > 4) {
            BeginDrawing();
            ClearBackground(BLUE);

            DrawTextPro(GetFontDefault(), "Game Over! Thanks for playing!", { 200, 500 }, { 0, 0 }, 0, 50.0f, 6.0f, PINK);

            EndDrawing();
            //continue;
            break;
        }


        for (const Target& food : foods) {
            DrawTexture(food.texture, food.posTarget.x - food.texture.width / 2, food.posTarget.y - food.texture.height / 2, getColorfromPoints(food));
        }

        EndDrawing();
    }

    UnloadTexture(puffTexture);
    UnloadTexture(angTexture);
    UnloadTexture(foodTexture);
    UnloadTexture(mutantTexture);

    CloseWindow();

    return 0;
}